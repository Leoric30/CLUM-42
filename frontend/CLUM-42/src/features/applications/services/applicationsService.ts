import api from '@/services/api'
import type { Application } from '@/types'

export async function getMyApplications(): Promise<Application[]> {
  const { data } = await api.get<Application[]>('/applications/mine')
  return data
}

export async function resubmitApplication(id: number, paymentProof: File): Promise<Application> {
  const form = new FormData()
  form.append('paymentProof', paymentProof)
  const { data } = await api.put<Application>(`/applications/${id}/resubmit`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
