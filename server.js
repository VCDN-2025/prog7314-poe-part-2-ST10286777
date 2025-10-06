import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import admin from "firebase-admin";
import { createRequire } from "module";

dotenv.config();

const require = createRequire(import.meta.url);
const serviceAccount = require("./config/serviceAccountKey.json");

const app = express();
app.use(cors());
app.use(express.json());

// Initialize Firebase Admin

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Verify Firebase Token Middleware
async function verifyToken(req, res, next) {
  const token = req.headers.authorization?.split("Bearer ")[1];
  if (!token) {
    return res.status(401).send({ error: "No token provided" });
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.user = decodedToken;
    next();
  } catch (error) {
    res.status(401).send({ error: "Invalid token" });
  }
}

// Example Protected Route
app.get("/profile", verifyToken, (req, res) => {
  res.send({ message: `Hello, ${req.user.email}!` });
});

// Example Public Route
app.get("/", (req, res) => {
  res.send("Node.js + Firebase Auth API is running");
});

// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
