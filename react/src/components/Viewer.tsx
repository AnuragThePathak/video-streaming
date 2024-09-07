import { Button, Card, TextInput } from "flowbite-react"
import { VideoJS } from "./VideoJS"
import { useState } from "react"

export default function Viewer() {
  const [videoId, setVideoId] = useState<string>("")
  const [fieldValue, setFieldValue] = useState<string>("")

  return (
    <Card>
      <h1 className="text-3xl font-semibold mb-4 tracking-tight text-gray-900 dark:text-white">
        Play a video
      </h1>
      <TextInput
        value={fieldValue}
        onChange={(e) => setFieldValue(e.target.value)}
        placeholder="Enter video ID"
        className="mb-4"
      />
      <Button onClick={() => setVideoId(fieldValue)}>Play</Button>
      {videoId && (
        <div className="video-container">
          <VideoJS
            src={`${process.env.SERVER_ADDRESS}/${videoId}/master.m3u8`}
          />
        </div>
      )}
    </Card>
  )
}
