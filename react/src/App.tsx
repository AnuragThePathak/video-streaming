import { Toaster } from "react-hot-toast"
import './App.css'
import VideoUploader from "./components/VideoUploader"
import Viewer from "./components/Viewer"

function App() {
  return (
    <>
      <div className="flex flex-col space-y-10">
        <h1 className="text-6xl">Video Streamer</h1>
        <VideoUploader />
        <Viewer />
        <Toaster />
      </div>
    </>
  )
}

export default App
