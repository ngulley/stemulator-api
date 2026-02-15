import React, { useRef, useEffect } from "react";
import { Organism } from "../types";

interface CanvasProps {
  organisms: Organism[];
  environment: string;
}

const Canvas: React.FC<CanvasProps> = ({ organisms, environment }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw background based on environment
    const bgGradients = {
      forest: ["#228B22", "#32CD32"],
      desert: ["#F4A460", "#DEB887"],
      arctic: ["#FFFFFF", "#F0F8FF"],
    };
    const [color1, color2] = bgGradients[
      environment as keyof typeof bgGradients
    ] || ["#228B22", "#32CD32"];
    const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height);
    gradient.addColorStop(0, color1);
    gradient.addColorStop(1, color2);
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Add subtle texture
    ctx.fillStyle = "rgba(255, 255, 255, 0.1)";
    for (let i = 0; i < 50; i++) {
      const x = Math.random() * canvas.width;
      const y = Math.random() * canvas.height;
      ctx.beginPath();
      ctx.arc(x, y, Math.random() * 2, 0, 2 * Math.PI);
      ctx.fill();
    }

    // Draw organisms with trait visualization
    organisms.forEach((org) => {
      if (!org.alive) return;

      // Color based on primary trait
      const isPredator = org.role === "predator";
      let fillColor = "";
      let outlineColor = "";

      if (isPredator) {
        // Predator: Dark red/brown with intimidating appearance
        fillColor = `rgba(139, 69, 19, 0.9)`; // Saddle brown
        outlineColor = `rgba(101, 50, 15, 1)`; // Darker brown
      } else {
        // Prey: Color based on traits
        const r = Math.floor(org.speed * 25.5);
        const g = Math.floor(org.camouflage * 25.5);
        const b = Math.floor(org.size * 25.5);
        fillColor = `rgba(${r}, ${g}, ${b}, 0.85)`;
        outlineColor = `rgba(${r}, ${g}, ${b}, 1)`;
      }

      ctx.fillStyle = fillColor;
      ctx.strokeStyle = outlineColor;
      ctx.lineWidth = isPredator ? 3 : 1.5;

      if (isPredator) {
        // Draw predator as triangle (pointing up) - looks like wolf head
        const size = org.size + 4;
        ctx.beginPath();
        ctx.moveTo(org.x, org.y - size - 2);
        ctx.lineTo(org.x - size, org.y + size);
        ctx.lineTo(org.x + size, org.y + size);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // Add eye circles for predator
        ctx.fillStyle = "rgba(255, 255, 255, 0.9)";
        ctx.beginPath();
        ctx.arc(
          org.x - size * 0.5,
          org.y - size * 0.3,
          size * 0.4,
          0,
          2 * Math.PI,
        );
        ctx.fill();
        ctx.beginPath();
        ctx.arc(
          org.x + size * 0.5,
          org.y - size * 0.3,
          size * 0.4,
          0,
          2 * Math.PI,
        );
        ctx.fill();

        // Add pupils (glowing red for predator)
        ctx.fillStyle = "rgba(255, 0, 0, 0.8)";
        ctx.beginPath();
        ctx.arc(
          org.x - size * 0.5,
          org.y - size * 0.3,
          size * 0.15,
          0,
          2 * Math.PI,
        );
        ctx.fill();
        ctx.beginPath();
        ctx.arc(
          org.x + size * 0.5,
          org.y - size * 0.3,
          size * 0.15,
          0,
          2 * Math.PI,
        );
        ctx.fill();

        // Add teeth/mouth
        ctx.strokeStyle = outlineColor;
        ctx.lineWidth = 1.5;
        for (let i = -1; i <= 1; i++) {
          ctx.beginPath();
          ctx.moveTo(org.x + i * size * 0.4, org.y + size - 2);
          ctx.lineTo(org.x + i * size * 0.3, org.y + size + 3);
          ctx.stroke();
        }

        // Add ears (small triangles)
        ctx.fillStyle = "rgba(101, 50, 15, 1)";
        // Left ear
        ctx.beginPath();
        ctx.moveTo(org.x - size * 0.7, org.y - size * 0.8);
        ctx.lineTo(org.x - size * 0.5, org.y - size * 0.4);
        ctx.lineTo(org.x - size * 0.8, org.y - size * 0.5);
        ctx.closePath();
        ctx.fill();
        // Right ear
        ctx.beginPath();
        ctx.moveTo(org.x + size * 0.7, org.y - size * 0.8);
        ctx.lineTo(org.x + size * 0.5, org.y - size * 0.4);
        ctx.lineTo(org.x + size * 0.8, org.y - size * 0.5);
        ctx.closePath();
        ctx.fill();
      } else {
        // Draw prey as circle
        ctx.beginPath();
        ctx.arc(org.x, org.y, org.size + 2, 0, 2 * Math.PI);
        ctx.fill();
        ctx.stroke();
      }
    });
  }, [organisms, environment]);

  // Animation loop to move organisms
  useEffect(() => {
    const interval = setInterval(() => {
      organisms.forEach((org) => {
        if (!org.alive) return;
        org.x += (Math.random() - 0.5) * org.speed * 0.5;
        org.y += (Math.random() - 0.5) * org.speed * 0.5;
        org.x = Math.max(0, Math.min(800, org.x));
        org.y = Math.max(0, Math.min(600, org.y));
      });
    }, 100);
    return () => clearInterval(interval);
  }, [organisms]);

  return <canvas ref={canvasRef} width={800} height={600} className="border" />;
};

export default Canvas;
