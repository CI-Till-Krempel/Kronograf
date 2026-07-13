
from fastapi import FastAPI
from typing import List
import uvicorn

from .models import Project

app = FastAPI()

# In-memory database
db: List[Project] = []

@app.post("/api/projects", response_model=Project)
def create_project(project: Project):
    db.append(project)
    return project

@app.get("/api/projects", response_model=List[Project])
def get_projects():
    return db

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
