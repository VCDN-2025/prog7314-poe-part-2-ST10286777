import express from "express";
import cors from "cors";
import dotenv from "dotenv";

// Import routes and middleware
import verifyToken from "./middleware/auth.js";
import questionRoutes from "./routes/questions.js";
import seedRoutes from "./routes/seed.js";


dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());

// Protected Question Routes
app.use("/api/questions", verifyToken, questionRoutes);

app.use('/api/seed', seedRoutes);

app.get("/profile", verifyToken, (req, res) => {
  
  res.json({
    success: true,
    data: {
      user: {
        id: req.user.id,        
        email: req.user.email,
      
        
      }
    },
    message: `Hello, ${req.user.email}!`
  });
});

// Health check to determine if the API is running
app.get("/", (req, res) => {
  res.json({ 
    success: true,
    message: "Trivia API is running!",
    timestamp: new Date().toISOString()
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: 'Route not found',
    path: req.path // Optional: show which path was requested
  });
});


// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
