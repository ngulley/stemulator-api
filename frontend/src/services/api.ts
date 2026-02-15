import { ScienceLab } from "../types";

// API base URL - defaults to localhost:8080 for development
const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/stemulator/v1";

// Request/Response types for AI guidance
export interface ScienceGuideRequest {
  studentName: string;
  setup: string[];
  observations: string[];
  evidence: string[];
  predictions: string[];
}

export interface ScienceGuideResponse {
  guidance: string;
}

/**
 * Fetch all available science labs
 */
export async function getLabs(): Promise<ScienceLab[]> {
  const response = await fetch(`${API_BASE_URL}/labs`);
  if (!response.ok) {
    throw new Error(`Failed to fetch labs: ${response.statusText}`);
  }
  return response.json();
}

/**
 * Fetch a specific science lab by ID
 */
export async function getLab(labId: string): Promise<ScienceLab | null> {
  const response = await fetch(`${API_BASE_URL}/labs/${labId}`);
  if (response.status === 404) {
    return null;
  }
  if (!response.ok) {
    throw new Error(`Failed to fetch lab: ${response.statusText}`);
  }
  return response.json();
}

/**
 * Get AI guidance for a lab part
 * @param labId - The lab identifier
 * @param partId - The lab part number
 * @param request - Student's responses (setup, observations, evidence, predictions)
 * @param evidenceFile - Optional CSV file with evidence data
 */
export async function getGuidance(
  labId: string,
  partId: number,
  request: ScienceGuideRequest,
  evidenceFile?: File,
): Promise<ScienceGuideResponse> {
  const formData = new FormData();
  formData.append("scienceGuideRequest", JSON.stringify(request));

  if (evidenceFile) {
    formData.append("evidence", evidenceFile);
  }

  const response = await fetch(
    `${API_BASE_URL}/guides/lab/${labId}/part/${partId}`,
    {
      method: "POST",
      body: formData,
    },
  );

  if (!response.ok) {
    throw new Error(`Failed to get guidance: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Create a new science lab (requires multipart form data with screenshot)
 */
export async function createLab(
  labId: string,
  discipline: string,
  topic: string,
  subTopic: string,
  expertise: string,
  simulation: string,
  screenshot: File,
): Promise<ScienceLab> {
  const formData = new FormData();
  formData.append("labId", labId);
  formData.append("discipline", discipline);
  formData.append("topic", topic);
  formData.append("subTopic", subTopic);
  formData.append("expertise", expertise);
  formData.append("simulation", simulation);
  formData.append("screenshot", screenshot);

  const response = await fetch(`${API_BASE_URL}/labs`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error(`Failed to create lab: ${response.statusText}`);
  }

  return response.json();
}

/**
 * Check if the backend API is available
 */
export async function checkApiHealth(): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/labs`, {
      method: "HEAD",
    });
    return response.ok;
  } catch {
    return false;
  }
}
