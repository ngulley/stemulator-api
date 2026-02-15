import { Organism, SimulationState, ScienceLab } from "./types";

export class Simulation {
  private state: SimulationState;

  constructor() {
    this.state = {
      generation: 0,
      organisms: this.initializePopulation(),
      environment: "forest",
      predation: "medium",
      foodAvailability: "medium",
      mutationRate: 5,
      populationHistory: [],
      traitDistribution: { speed: [], camouflage: [], size: [] },
      survivalRate: 0,
      actions: [],
    };
  }

  private initializePopulation(
    count = 50,
    options?: { environment?: string; favorWhiteFur?: boolean },
  ): Organism[] {
    const organisms: Organism[] = [];

    // Initialize prey (85% of population)
    const preyCount = Math.floor(count * 0.85);
    for (let i = 0; i < preyCount; i++) {
      let baseSpeed = Math.random() * 10;
      let baseCamouflage = Math.random() * 10;
      let baseSize = Math.random() * 10;

      if (options?.environment === "desert") {
        baseSpeed = Math.min(10, baseSpeed + Math.random() * 2);
        baseSize = Math.max(0, baseSize - Math.random() * 3);
      } else if (options?.environment === "arctic") {
        baseSize = Math.min(10, baseSize + Math.random() * 3);
      }

      if (options?.favorWhiteFur && options.environment === "arctic") {
        baseCamouflage = Math.max(7, baseCamouflage);
      }

      organisms.push({
        id: i,
        x: Math.random() * 800,
        y: Math.random() * 600,
        speed: Math.max(0, Math.min(10, baseSpeed)),
        camouflage: Math.max(0, Math.min(10, baseCamouflage)),
        size: Math.max(0, Math.min(10, baseSize)),
        alive: true,
        role: "prey",
      });
    }

    // Initialize predators (15% of population)
    const predatorCount = count - preyCount;
    for (let i = 0; i < predatorCount; i++) {
      organisms.push({
        id: preyCount + i,
        x: Math.random() * 800,
        y: Math.random() * 600,
        speed: Math.random() * 10,
        camouflage: Math.random() * 10,
        size: Math.random() * 10,
        alive: true,
        role: "predator",
      });
    }

    return organisms;
  }

  getState(): SimulationState {
    return { ...this.state };
  }

  updateSettings(
    settings: Partial<
      Pick<
        SimulationState,
        "environment" | "predation" | "foodAvailability" | "mutationRate"
      >
    >,
  ) {
    Object.assign(this.state, settings);
    this.state.actions.push(`Settings updated: ${JSON.stringify(settings)}`);
  }

  applyScienceLab(lab: ScienceLab, partId?: number) {
    const parts = partId
      ? lab.labParts.filter((p) => p.partId === partId)
      : lab.labParts;

    const combinedSettings: Partial<
      Pick<
        SimulationState,
        "environment" | "predation" | "foodAvailability" | "mutationRate"
      >
    > = {};
    let favorWhiteFur = false;

    parts.forEach((part) => {
      part.setup.forEach((line) => {
        const text = line.toLowerCase();
        if (text.includes("desert")) combinedSettings.environment = "desert";
        if (
          text.includes("snow") ||
          text.includes("snowy") ||
          text.includes("arctic")
        )
          combinedSettings.environment = "arctic";
        if (text.includes("rocky")) combinedSettings.environment = "desert";
        if (text.includes("mix of fur") || text.includes("fur colors")) {
          // keep default mixed population
        }
        if (
          text.includes("wolves") ||
          text.includes("predator") ||
          text.includes("introduce wolves")
        ) {
          combinedSettings.predation = "high";
        }
        if (text.includes("food availability") && text.includes("tough")) {
          combinedSettings.foodAvailability = "low";
        }
        if (text.includes("tough food"))
          combinedSettings.foodAvailability = "low";
        if (text.includes("mutat")) combinedSettings.mutationRate = 8;
        if (text.includes("white fur")) favorWhiteFur = true;
        if (
          text.includes("observe the population changes over 10 generations") &&
          !combinedSettings.mutationRate
        ) {
          // keep mutation rate as-is unless mutations are specified
        }
      });
    });

    // Apply reasonable defaults if not specified
    if (!combinedSettings.environment)
      combinedSettings.environment = this.state.environment;
    if (!combinedSettings.predation)
      combinedSettings.predation = this.state.predation;
    if (!combinedSettings.foodAvailability)
      combinedSettings.foodAvailability = this.state.foodAvailability;
    if (!combinedSettings.mutationRate && combinedSettings.mutationRate !== 0)
      combinedSettings.mutationRate = this.state.mutationRate;

    // Update state settings
    this.updateSettings(combinedSettings);

    // Reinitialize population to reflect environment / favored traits
    this.state.organisms = this.initializePopulation(50, {
      environment: combinedSettings.environment,
      favorWhiteFur,
    });

    this.state.populationHistory = [];
    this.state.actions.push(`Applied lab ${lab._id} part ${partId ?? "all"}`);
  }

  runGeneration() {
    this.state.generation++;
    this.survive();
    this.reproduce();
    this.updateStats();
    this.state.actions.push(`Generation ${this.state.generation} completed`);
  }

  private survive() {
    const predationMultiplier = { low: 0.3, medium: 0.5, high: 0.7 }[
      this.state.predation
    ];
    const foodMultiplier = { low: 0.7, medium: 0.5, high: 0.3 }[
      this.state.foodAvailability
    ];
    const envBonus = {
      forest: { speed: 0, camouflage: 2, size: 0 },
      desert: { speed: 1, camouflage: 0, size: -1 },
      arctic: { speed: 0, camouflage: 0, size: 1 },
    }[this.state.environment];

    this.state.organisms.forEach((org) => {
      if (!org.alive) return;
      let survivalProb = 0.5; // base
      survivalProb += (org.speed + envBonus.speed) * 0.05;
      survivalProb += (org.camouflage + envBonus.camouflage) * 0.05;
      survivalProb += (org.size + envBonus.size) * 0.02;
      survivalProb -= predationMultiplier;
      survivalProb -= foodMultiplier;
      survivalProb = Math.max(0, Math.min(1, survivalProb));
      org.alive = Math.random() < survivalProb;
    });
  }

  private reproduce() {
    const survivors = this.state.organisms.filter((o) => o.alive);
    const newOrganisms: Organism[] = [];
    survivors.forEach((parent) => {
      const child: Organism = {
        id: this.state.organisms.length + newOrganisms.length,
        x: Math.random() * 800,
        y: Math.random() * 600,
        speed: Math.max(
          0,
          Math.min(
            10,
            parent.speed +
              ((Math.random() - 0.5) * this.state.mutationRate) / 10,
          ),
        ),
        camouflage: Math.max(
          0,
          Math.min(
            10,
            parent.camouflage +
              ((Math.random() - 0.5) * this.state.mutationRate) / 10,
          ),
        ),
        size: Math.max(
          0,
          Math.min(
            10,
            parent.size +
              ((Math.random() - 0.5) * this.state.mutationRate) / 10,
          ),
        ),
        alive: true,
        role: parent.role,
      };
      newOrganisms.push(child);
    });
    this.state.organisms = newOrganisms;
  }

  private updateStats() {
    const alive = this.state.organisms.filter((o) => o.alive).length;
    this.state.populationHistory.push(alive);
    this.state.survivalRate = alive / this.state.organisms.length;

    this.state.traitDistribution = {
      speed: this.state.organisms.map((o) => o.speed),
      camouflage: this.state.organisms.map((o) => o.camouflage),
      size: this.state.organisms.map((o) => o.size),
    };
  }

  reset() {
    this.state = {
      generation: 0,
      organisms: this.initializePopulation(),
      environment: "forest",
      predation: "medium",
      foodAvailability: "medium",
      mutationRate: 5,
      populationHistory: [],
      traitDistribution: { speed: [], camouflage: [], size: [] },
      survivalRate: 0,
      actions: [],
    };
  }

  getLabSnapshot(): any {
    return {
      environment: this.state.environment,
      parameters: {
        predation: this.state.predation,
        foodAvailability: this.state.foodAvailability,
        mutationRate: this.state.mutationRate,
      },
      currentPopulation: this.state.organisms.filter((o) => o.alive).length,
      last10Actions: this.state.actions.slice(-10),
    };
  }
}
