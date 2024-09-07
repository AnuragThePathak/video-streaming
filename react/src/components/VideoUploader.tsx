import React, { useState } from 'react'
import { upload } from "../server-requests"
import Status from "../Status"
import { Button, Card, FileInput, Label, Progress, Textarea, TextInput, Toast } from "flowbite-react"
import { HiCheck } from "react-icons/hi2"

export default function VideoUploader() {
  const [file, setFile] = useState<File | null>(null)
  const [dragging, setDragging] = useState<boolean>(false)
  const [title, setTitle] = useState<string>('')
  const [description, setDescription] = useState<string>('')
  const [status, setStatus] = useState<number>(Status.Editing)
  const [progress, setProgress] = useState<number>(0)
  const [videoId, setVideoId] = useState<string>("")

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0])
    }
  }

  const handleDragOver = (event: React.DragEvent<HTMLLabelElement>) => {
    event.preventDefault()
    event.stopPropagation()
    setDragging(true)
  }

  const handleDragEnter = (event: React.DragEvent<HTMLLabelElement>) => {
    event.preventDefault()
    event.stopPropagation()
    setDragging(true)
  }

  const handleDragLeave = (event: React.DragEvent<HTMLLabelElement>) => {
    event.preventDefault()
    event.stopPropagation()
    setDragging(false)
  }

  const handleDrop = (event: React.DragEvent<HTMLLabelElement>) => {
    event.preventDefault()
    event.stopPropagation()
    setDragging(false)
    if (event.dataTransfer.files && event.dataTransfer.files[0]) {
      setFile(event.dataTransfer.files[0])
    }
  }

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!file) {
      alert('Please select a file')
      return
    }
    if (!title) {
      alert('Please enter a title')
      return
    }
    if (!description) {
      alert('Please enter a description')
      return
    }
    const formData = new FormData()
    formData.append('title', title)
    formData.append('description', description)
    formData.append('file', file)
    setStatus(Status.Uploading)
    setVideoId((
      await upload(formData, (progress) => setProgress(progress))
    ).videoId)
    reset()
  }

  const reset = () => {
    setFile(null)
    setTitle('')
    setDescription('')
    setStatus(Status.Editing)
    setProgress(0)
  }

  return (
    <Card>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <Label htmlFor="title" className="block text-sm font-medium">Title</Label>
          <TextInput
            type="text"
            id="title"
            name="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div>
          <Label htmlFor="description" className="block text-sm font-medium">Description</Label>
          <Textarea
            id="description"
            name="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />
        </div>
        <div className="flex items-center justify-center w-full">
          <Label
            htmlFor="dropzone-file"
            className={`flex flex-col items-center justify-center w-full h-64 border-2 ${dragging ? 'border-blue-500' : 'border-gray-300'} border-dashed rounded-lg cursor-pointer bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500 dark:hover:bg-gray-600`}
            onDragOver={handleDragOver}
            onDragEnter={handleDragEnter}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
          >
            <div className="flex flex-col items-center justify-center pt-5 pb-6">
              <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2" />
              </svg>
              <p className="mb-2 text-sm text-gray-500 dark:text-gray-400"><span className="font-semibold">Click to upload</span> or drag and drop</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">MP4 or WebM (MAX. 800x400px)</p>
            </div>
            <FileInput id="dropzone-file" accept="video/mp4,video/webm"
              className="hidden"
              onChange={handleFileChange} />
          </Label>
        </div>
        {file && (
          <div className="mt-4 text-center">
            <p className="text-sm text-gray-500 dark:text-gray-400">File selected: <span className="font-semibold">{file.name}</span></p>
          </div>
        )}
        {status === Status.Uploading && (
          <Progress progress={progress} size="lg" />
        )}
        {videoId && (
          <Toast>
            <div className="inline-flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-cyan-100 text-cyan-500 dark:bg-cyan-800 dark:text-cyan-200">
              <HiCheck className="h-5 w-5" />
            </div>
            <div className="ml-3 text-sm font-normal">
              {`Video uploaded successfully! Video ID: ${videoId}`}
            </div>
            <Toast.Toggle onDismiss={() => setVideoId("")} />
          </Toast>
        )}
        <div className="text-center">
          <Button type="submit" gradientDuoTone="redToYellow"
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm">
            Submit
          </Button>
        </div>
      </form>
    </Card>
  )
}