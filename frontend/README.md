# STEMulator

An AI-guided virtual STEM lab platform with interactive courses and simulations.

## Features

- **Home Page**: Hero section, explore modules by subject, featured labs
- **Courses**: Browse physics and chemistry courses with modules and lessons
- **Labs**: Interactive simulations with filters, including Natural Selection Simulator
- **Navigation**: Sticky navbar with logo, links, search, and create button
- **Responsive Design**: Clean UI with Tailwind CSS, consistent design system

## Tech Stack

- React + TypeScript
- React Router
- Tailwind CSS
- Recharts
- HTML5 Canvas
- Lucide React (icons)
- Vite

## Getting Started

1. Install dependencies: `npm install`
2. Run development server: `npm run dev`
3. Open http://localhost:5173

## Build

`npm run build`

## Pages

- `/` - Home
- `/courses` - Course listings
- `/courses/:id` - Course details with modules and labs
- `/labs` - Lab listings with filters
- `/labs/:labId` - Lab player (e.g., Natural Selection Simulator)
