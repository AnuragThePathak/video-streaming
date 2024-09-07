import axios from "axios"

export async function upload(formData: FormData, onProgress: (progress: number) => void) {
	const res = await axios.post(process.env.SERVER_ADDRESS!, formData, {
		onUploadProgress: (progressEvent) => {
			const { loaded, total } = progressEvent
			const precentage = Math.floor((loaded * 100) / (total ?? 1))
			onProgress(precentage)
		},
		headers: {
			'Content-Type': 'multipart/form-data'
		}
	})
	return res.data
}