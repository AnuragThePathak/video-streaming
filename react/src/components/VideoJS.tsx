import Hls from "hls.js"
import { useEffect, useRef } from "react"
import videojs from "video.js"
import Player from "video.js/dist/types/player"
import "video.js/dist/video-js.css"
import toast from "react-hot-toast"

interface VideoJSProps {
  src: string
}

export const VideoJS = ({ src }: VideoJSProps) => {
  const videoRef = useRef<HTMLVideoElement>(null)
  const playerRef = useRef<Player | null>(null)

  useEffect(() => {
    if (!videoRef.current) return

    // Initialize Video.js player if not already initialized
    if (!playerRef.current) {
      playerRef.current = videojs(videoRef.current, {
        controls: true,
        autoplay: true,
        preload: "auto",
        responsive: true,
        fluid: true, // This makes the player responsive
      })
    }

    if (Hls.isSupported()) {
      const hls = new Hls()
      hls.loadSource(src)
      hls.attachMedia(videoRef.current)

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        videoRef.current?.play()
      })

      return () => {
        hls.destroy() // Clean up Hls instance
      }
    } else if (videoRef.current.canPlayType("application/vnd.apple.mpegurl")) {
      videoRef.current.src = src
      videoRef.current.addEventListener("canplay", () => {
        videoRef.current?.play()
      })
    } else {
      console.error("Video format not supported")
      toast.error("Video format not supported")
    }

    return () => {
      if (playerRef.current && !playerRef.current.isDisposed()) {
        playerRef.current.dispose()
        playerRef.current = null
      }
    }
  }, [src])

  return (
    <div data-vjs-player>
      <video ref={videoRef} className="video-js vjs-default-skin vjs-control-bar" />
    </div>
  )
}
