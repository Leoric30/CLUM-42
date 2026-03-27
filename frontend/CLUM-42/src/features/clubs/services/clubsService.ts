import api from '@/services/api'
import type { Club } from '@/types'

export async function getClubs(): Promise<Club[]> {
  const { data } = await api.get<Club[]>('/clubs')
  return data
}

export async function applyToClub(clubId: number, paymentProof?: File): Promise<void> {
  const form = new FormData()
  form.append('clubId', String(clubId))
  if (paymentProof) {
    form.append('paymentProof', paymentProof)
  }
  await api.post('/applications', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
