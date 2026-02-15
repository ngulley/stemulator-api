import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  BarChart,
  Bar,
} from "recharts";
import { SimulationState } from "../types";

interface ResultsProps {
  state: SimulationState;
}

const Results: React.FC<ResultsProps> = ({ state }) => {
  const populationData = state.populationHistory.map((pop, idx) => ({
    generation: idx + 1,
    population: pop,
  }));

  const traitData = [
    {
      trait: "Speed",
      value:
        state.traitDistribution.speed.reduce((a, b) => a + b, 0) /
          state.traitDistribution.speed.length || 0,
    },
    {
      trait: "Camouflage",
      value:
        state.traitDistribution.camouflage.reduce((a, b) => a + b, 0) /
          state.traitDistribution.camouflage.length || 0,
    },
    {
      trait: "Size",
      value:
        state.traitDistribution.size.reduce((a, b) => a + b, 0) /
          state.traitDistribution.size.length || 0,
    },
  ];

  return (
    <div className="p-4 bg-gray-100 rounded">
      <h2 className="text-xl font-bold mb-4">Results</h2>
      <div className="mb-4">
        <h3>Population Size Over Generations</h3>
        <LineChart width={400} height={200} data={populationData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="generation" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="population" stroke="#8884d8" />
        </LineChart>
      </div>
      <div className="mb-4">
        <h3>Trait Distribution (Average)</h3>
        <BarChart width={400} height={200} data={traitData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="trait" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="value" fill="#82ca9d" />
        </BarChart>
      </div>
      <div>
        <p>Survival Rate: {(state.survivalRate * 100).toFixed(2)}%</p>
      </div>
    </div>
  );
};

export default Results;
