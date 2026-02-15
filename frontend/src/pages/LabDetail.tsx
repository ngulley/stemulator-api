import React, { useState, useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import { Home, Menu, X, Loader2 } from "lucide-react";
import PageShell from "../components/PageShell";
import Canvas from "../components/Canvas";
import Controls from "../components/Controls";
import Results from "../components/Results";
import AIPanel from "../components/AIPanel";
import LabSidebar from "../components/LabSidebar";
import LabContentPanel from "../components/LabContentPanel";
import AICoacHEvaluator from "../components/AICoacHEvaluator";
import { Simulation } from "../simulation";
import { SimulationState, LabSnapshot, ScienceLab } from "../types";
import { mockLabs } from "../data";
import { getLab } from "../services/api";

const LabDetail: React.FC = () => {
  const { labId } = useParams<{ labId: string }>();
  const [lab, setLab] = useState<ScienceLab | null | undefined>(undefined);
  const [loading, setLoading] = useState(true);
  const [sim] = useState(() => new Simulation());
  const [state, setState] = useState<SimulationState>(sim.getState());
  const [activeTab, setActiveTab] = useState<"results" | "ai">("results");
  const [currentPartIdx, setCurrentPartIdx] = useState(0);
  const [studentResponses, setStudentResponses] = useState<Record<
    string,
    string
  > | null>(null);
  const [showSidebar, setShowSidebar] = useState(false);

  // Fetch lab data from API with fallback to mock
  useEffect(() => {
    async function fetchLab() {
      if (!labId) return;
      setLoading(true);
      try {
        const data = await getLab(labId);
        if (data) {
          // Normalize data from backend
          setLab({
            ...data,
            title: data.title || data.subTopic || data._id,
            difficulty: data.difficulty || "Intermediate",
          });
        } else {
          // Fallback to mock data
          const mockLab = mockLabs.find((l) => l._id === labId);
          setLab(mockLab || null);
        }
      } catch (err) {
        console.warn("Failed to fetch from API, using mock data:", err);
        const mockLab = mockLabs.find((l) => l._id === labId);
        setLab(mockLab || null);
      } finally {
        setLoading(false);
      }
    }
    fetchLab();
  }, [labId]);

  useEffect(() => {
    if (lab && lab.labParts[currentPartIdx]) {
      sim.applyScienceLab(lab, lab.labParts[currentPartIdx].partId);
      setState(sim.getState());
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPartIdx, lab]);

  useEffect(() => {
    const interval = setInterval(() => {
      setState(sim.getState());
    }, 100);
    return () => clearInterval(interval);
  }, [sim]);

  const handleUpdateSettings = (
    settings: Partial<
      Pick<
        SimulationState,
        "environment" | "predation" | "foodAvailability" | "mutationRate"
      >
    >,
  ) => {
    sim.updateSettings(settings);
    setState(sim.getState());
  };

  const handleRunGeneration = () => {
    sim.runGeneration();
    setState(sim.getState());
  };

  const handleReset = () => {
    sim.reset();
    if (lab && lab.labParts[currentPartIdx]) {
      sim.applyScienceLab(lab, lab.labParts[currentPartIdx].partId);
      setState(sim.getState());
    }
  };

  const handleSendSnapshot = (snapshot: LabSnapshot) => {
    console.log("Sending snapshot:", snapshot);
  };

  const handleObservationsSubmit = (responses: Record<string, string>) => {
    setStudentResponses(responses);
    setActiveTab("ai");
  };

  if (loading) {
    return (
      <PageShell>
        <div className="flex flex-col items-center justify-center min-h-screen">
          <Loader2 className="h-10 w-10 animate-spin text-blue-600 mb-4" />
          <p className="text-slate-600">Loading lab...</p>
        </div>
      </PageShell>
    );
  }

  if (!lab) {
    return (
      <PageShell>
        <div className="flex flex-col items-center justify-center min-h-screen text-center">
          <h2 className="text-2xl font-bold text-slate-900 mb-2">
            Lab not found
          </h2>
          <p className="text-slate-600 mb-6">
            The lab you're looking for doesn't exist.
          </p>
          <Link
            to="/"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Go Back Home
          </Link>
        </div>
      </PageShell>
    );
  }

  return (
    <PageShell>
      <div className="h-full flex flex-col">
        {/* Top Bar: Minimal Navigation */}
        <div className="bg-white border-b border-slate-200 px-4 py-3 flex items-center justify-between flex-shrink-0">
          <div className="flex items-center gap-3">
            <button
              onClick={() => setShowSidebar(!showSidebar)}
              className="p-2 hover:bg-slate-100 rounded-lg transition-colors"
            >
              {showSidebar ? (
                <X className="w-5 h-5 text-slate-600" />
              ) : (
                <Menu className="w-5 h-5 text-slate-600" />
              )}
            </button>
            <div className="flex items-center gap-2 text-sm">
              <Link
                to="/"
                className="text-slate-600 hover:text-blue-600 transition-colors"
              >
                <Home className="w-4 h-4" />
              </Link>
              <span className="text-slate-300">/</span>
              <Link
                to="/labs"
                className="text-slate-600 hover:text-blue-600 transition-colors"
              >
                Labs
              </Link>
              <span className="text-slate-300">/</span>
              <span className="font-medium text-slate-900">{lab.title}</span>
            </div>
          </div>
          <div className="text-xs bg-blue-100 text-blue-700 px-3 py-1 rounded-full font-semibold">
            {lab.discipline}
          </div>
        </div>

        {/* Main Content - Full Viewport Layout */}
        <div className="flex-1 flex overflow-hidden gap-0">
          {/* Collapsible Sidebar */}
          {showSidebar && (
            <div className="w-80 flex-shrink-0 bg-white border-r border-slate-200 overflow-hidden flex flex-col">
              <LabSidebar
                lab={lab}
                currentPartIdx={currentPartIdx}
                onPartChange={setCurrentPartIdx}
              />
            </div>
          )}

          {/* Left: Instructions Panel - Fixed Width */}
          <div className="w-80 flex-shrink-0 bg-white border-r border-slate-200 overflow-hidden flex flex-col">
            <div className="px-4 py-3 border-b border-slate-200 bg-blue-50 flex-shrink-0">
              <h3 className="font-bold text-slate-900 text-sm">
                Lab Instructions
              </h3>
              <p className="text-xs text-slate-600 mt-1">
                Part {currentPartIdx + 1} of {lab.labParts.length}
              </p>
            </div>
            <div className="flex-1 p-3 overflow-y-auto">
              <LabContentPanel
                part={lab.labParts[currentPartIdx]}
                partNumber={currentPartIdx + 1}
                totalParts={lab.labParts.length}
                onObservationsSubmit={handleObservationsSubmit}
              />
            </div>
          </div>

          {/* Right: Simulation Area */}
          <div className="flex-1 flex flex-col overflow-hidden">
            {/* Top: Main Canvas (60% height) */}
            <div className="flex-[1.5] bg-white border-b border-slate-200 p-3 flex flex-col overflow-hidden min-h-0">
              <div className="flex items-center justify-between mb-2 flex-shrink-0">
                <h2 className="font-bold text-slate-900 text-sm">
                  Live Simulation
                </h2>
                <div className="flex gap-3 text-sm">
                  <span className="text-slate-600">
                    Gen:{" "}
                    <span className="font-bold text-blue-600">
                      {state.generation}
                    </span>
                  </span>
                  <span className="text-slate-600">
                    Pop:{" "}
                    <span className="font-bold text-green-600">
                      {state.organisms.length}
                    </span>
                  </span>
                  <span className="text-slate-600">
                    Survival:{" "}
                    <span className="font-bold text-purple-600">
                      {(state.survivalRate * 100).toFixed(0)}%
                    </span>
                  </span>
                </div>
              </div>
              <div className="flex-1 flex flex-col min-h-0">
                <Canvas
                  organisms={state.organisms}
                  environment={state.environment}
                />
              </div>
              <div className="mt-2 pt-2 border-t border-slate-200 flex justify-center gap-4 text-sm flex-shrink-0">
                <div className="flex items-center gap-2">
                  <div className="w-2 h-2 bg-red-500 rounded-full"></div>
                  <span>Speed</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                  <span>Camouflage</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                  <span>Size</span>
                </div>
              </div>
            </div>

            {/* Bottom: Controls & Feedback Side by Side (40% height) */}
            <div className="flex-1 flex gap-3 p-3 overflow-hidden min-h-0">
              {/* Left: Controls Panel */}
              <div className="flex-[0.8] bg-white border border-slate-200 flex flex-col overflow-hidden min-h-0">
                <h2 className="font-bold text-slate-900 text-sm px-3 py-2 border-b border-slate-200 bg-slate-50 flex-shrink-0">
                  Experiment Controls
                </h2>
                <div className="flex-1 p-3">
                  <Controls
                    state={state}
                    onUpdateSettings={handleUpdateSettings}
                    onRunGeneration={handleRunGeneration}
                    onReset={handleReset}
                  />
                </div>
              </div>

              {/* Right: Analysis/Feedback Panel */}
              <div className="flex-[1.2] bg-white border border-slate-200 flex flex-col overflow-hidden min-h-0">
                <div className="flex border-b border-slate-200 bg-slate-50 flex-shrink-0">
                  <button
                    onClick={() => setActiveTab("results")}
                    className={`flex-1 px-3 py-2 text-sm font-semibold transition-colors ${
                      activeTab === "results"
                        ? "text-blue-600 border-b-2 border-blue-600 bg-white"
                        : "text-slate-600 hover:text-slate-900"
                    }`}
                  >
                    Analysis
                  </button>
                  <button
                    onClick={() => setActiveTab("ai")}
                    className={`flex-1 px-3 py-2 text-sm font-semibold transition-colors ${
                      activeTab === "ai"
                        ? "text-blue-600 border-b-2 border-blue-600 bg-white"
                        : "text-slate-600 hover:text-slate-900"
                    }`}
                  >
                    Feedback
                  </button>
                </div>
                <div className="flex-1 p-3 text-sm overflow-y-auto">
                  {activeTab === "results" ? (
                    <Results state={state} />
                  ) : studentResponses ? (
                    <AICoacHEvaluator
                      lab={lab}
                      part={lab.labParts[currentPartIdx]}
                      studentResponses={studentResponses}
                    />
                  ) : (
                    <AIPanel
                      onSendSnapshot={handleSendSnapshot}
                      snapshot={sim.getLabSnapshot()}
                    />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PageShell>
  );
};

export default LabDetail;
