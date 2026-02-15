import React from "react";
import { SimulationState } from "../types";

interface ControlsProps {
  state: SimulationState;
  onUpdateSettings: (
    settings: Partial<
      Pick<
        SimulationState,
        "environment" | "predation" | "foodAvailability" | "mutationRate"
      >
    >,
  ) => void;
  onRunGeneration: () => void;
  onReset: () => void;
}

const Controls: React.FC<ControlsProps> = ({
  state,
  onUpdateSettings,
  onRunGeneration,
  onReset,
}) => {
  return (
    <div className="p-4 bg-gray-100 rounded">
      <h2 className="text-xl font-bold mb-4">Controls</h2>
      <div className="mb-4">
        <label className="block">Environment:</label>
        <select
          value={state.environment}
          onChange={(e) =>
            onUpdateSettings({ environment: e.target.value as any })
          }
          className="border p-2"
        >
          <option value="forest">Forest</option>
          <option value="desert">Desert</option>
          <option value="arctic">Arctic</option>
        </select>
      </div>
      <div className="mb-4">
        <label className="block">Predation:</label>
        <select
          value={state.predation}
          onChange={(e) =>
            onUpdateSettings({ predation: e.target.value as any })
          }
          className="border p-2"
        >
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
        </select>
      </div>
      <div className="mb-4">
        <label className="block">Food Availability:</label>
        <select
          value={state.foodAvailability}
          onChange={(e) =>
            onUpdateSettings({ foodAvailability: e.target.value as any })
          }
          className="border p-2"
        >
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
        </select>
      </div>
      <div className="mb-4">
        <label className="block">Mutation Rate: {state.mutationRate}%</label>
        <input
          type="range"
          min="0"
          max="10"
          value={state.mutationRate}
          onChange={(e) =>
            onUpdateSettings({ mutationRate: parseInt(e.target.value) })
          }
          className="w-full"
        />
      </div>
      <div className="flex gap-2">
        <button
          onClick={onRunGeneration}
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          Run Generation
        </button>
        <button
          onClick={onReset}
          className="bg-red-500 text-white px-4 py-2 rounded"
        >
          Reset
        </button>
      </div>
    </div>
  );
};

export default Controls;
