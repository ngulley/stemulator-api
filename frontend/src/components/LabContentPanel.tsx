import React, { useState } from "react";
import {
  ChevronRight,
  CheckSquare,
  AlertCircle,
  Lightbulb,
  Play,
} from "lucide-react";
import { LabPart } from "../types";
import StudentResponses from "./StudentResponses";

interface LabContentPanelProps {
  part: LabPart;
  partNumber: number;
  totalParts: number;
  onObservationsSubmit?: (responses: Record<string, string>) => void;
}

const LabContentPanel: React.FC<LabContentPanelProps> = ({
  part,
  partNumber,
  totalParts,
  onObservationsSubmit,
}) => {
  const [completedItems, setCompletedItems] = useState<Record<string, boolean>>(
    {},
  );
  const [activeSection, setActiveSection] = useState<string>("setup");

  const toggleComplete = (itemId: string) => {
    setCompletedItems((prev) => ({
      ...prev,
      [itemId]: !prev[itemId],
    }));
  };

  const handleObservationsSubmit = (responses: Record<string, string>) => {
    onObservationsSubmit?.(responses);
  };

  const sections = [
    {
      id: "setup",
      label: "Setup Instructions",
      icon: <Play className="w-5 h-5" />,
      color: "blue",
      items: part.setup,
      description: "Follow these steps to set up the experiment",
    },
    {
      id: "observations",
      label: "Observations",
      icon: <AlertCircle className="w-5 h-5" />,
      color: "amber",
      items: part.observations,
      description: "Record what you observe during the simulation",
      isInteractive: true,
    },
    {
      id: "evidence",
      label: "Evidence",
      icon: <CheckSquare className="w-5 h-5" />,
      color: "purple",
      items: part.evidence,
      description: "Collect and track important evidence",
    },
    {
      id: "predictions",
      label: "Analysis & Predictions",
      icon: <Lightbulb className="w-5 h-5" />,
      color: "green",
      items: part.predictions,
      description: "Make predictions based on your findings",
    },
  ];

  const colorClasses = {
    blue: "from-blue-50 to-blue-100 border-blue-300 text-blue-900",
    amber: "from-amber-50 to-amber-100 border-amber-300 text-amber-900",
    purple: "from-purple-50 to-purple-100 border-purple-300 text-purple-900",
    green: "from-green-50 to-green-100 border-green-300 text-green-900",
  };

  const iconColorClasses = {
    blue: "text-blue-600 bg-blue-100",
    amber: "text-amber-600 bg-amber-100",
    purple: "text-purple-600 bg-purple-100",
    green: "text-green-600 bg-green-100",
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-gradient-to-r from-slate-900 to-slate-800 rounded-xl p-6 text-white">
        <div className="flex items-start justify-between mb-3">
          <div>
            <h2 className="text-3xl font-bold mb-2">{part.title}</h2>
            <p className="text-slate-300">
              Part {partNumber} of {totalParts}
            </p>
          </div>
          <div className="text-right">
            <div className="text-sm text-slate-400">Progress</div>
            <div className="text-2xl font-bold">
              {Math.round((partNumber / totalParts) * 100)}%
            </div>
          </div>
        </div>
        <div className="w-full bg-slate-700 rounded-full h-2 overflow-hidden">
          <div
            className="bg-blue-500 h-full transition-all rounded-full"
            style={{ width: `${(partNumber / totalParts) * 100}%` }}
          />
        </div>
      </div>

      {/* Sections */}
      <div className="space-y-4">
        {sections.map((section) => (
          <div
            key={section.id}
            className="bg-white rounded-lg border border-slate-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow"
          >
            {/* Section Header */}
            <button
              onClick={() =>
                setActiveSection(activeSection === section.id ? "" : section.id)
              }
              className={`w-full px-6 py-4 flex items-center justify-between bg-gradient-to-r ${colorClasses[section.color as keyof typeof colorClasses]} transition-colors`}
            >
              <div className="flex items-center gap-3">
                <div
                  className={`p-2 rounded-lg ${iconColorClasses[section.color as keyof typeof iconColorClasses]}`}
                >
                  {section.icon}
                </div>
                <div className="text-left">
                  <h3 className="font-semibold text-lg">{section.label}</h3>
                  <p className="text-sm opacity-75">{section.description}</p>
                </div>
              </div>
              <ChevronRight
                className={`w-6 h-6 transition-transform flex-shrink-0 ${
                  activeSection === section.id ? "rotate-90" : ""
                }`}
              />
            </button>

            {/* Section Content */}
            {activeSection === section.id && (
              <div className="px-6 py-4 border-t border-slate-200 bg-slate-50">
                {section.id === "observations" && section.isInteractive ? (
                  <StudentResponses
                    part={part}
                    onSubmit={handleObservationsSubmit}
                  />
                ) : (
                  <div className="space-y-3">
                    {section.items.map((item, idx) => {
                      const itemId = `${section.id}-${idx}`;
                      const isCompleted = completedItems[itemId];

                      return (
                        <div
                          key={idx}
                          className="flex gap-3 p-3 bg-white rounded-lg border border-slate-100 hover:border-slate-300 transition-colors group"
                        >
                          {(section.id === "evidence" ||
                            section.id === "observations") && (
                            <button
                              onClick={() => toggleComplete(itemId)}
                              className="pt-1 flex-shrink-0"
                            >
                              <div
                                className={`w-5 h-5 rounded border-2 flex items-center justify-center transition-all ${
                                  isCompleted
                                    ? "bg-green-500 border-green-500"
                                    : "border-slate-300 group-hover:border-slate-400"
                                }`}
                              >
                                {isCompleted && (
                                  <span className="text-white text-sm font-bold">
                                    ✓
                                  </span>
                                )}
                              </div>
                            </button>
                          )}
                          <div className="flex-1 min-w-0">
                            {section.id === "setup" ||
                            section.id === "predictions" ? (
                              <div>
                                <span className="font-semibold text-slate-900">
                                  {String.fromCharCode(65 + idx)}.
                                </span>{" "}
                                <span className="text-slate-700">{item}</span>
                              </div>
                            ) : (
                              <div>
                                <span className="font-semibold text-slate-900">
                                  {String.fromCharCode(65 + idx)}.
                                </span>{" "}
                                <span
                                  className={`${isCompleted ? "text-slate-400 line-through" : "text-slate-700"}`}
                                >
                                  {item}
                                </span>
                              </div>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default LabContentPanel;
