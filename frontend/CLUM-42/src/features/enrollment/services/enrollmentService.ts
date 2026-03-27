import api from '@/services/api'
import type { Application, EnrollmentRequest, Page, RequestStatus } from '@/types'

/** GET /api/applications/club/{clubId}?status=&page=&size= */
export async function getClubApplications(
  clubId: number,
  status?: RequestStatus | 'TODAS',
  page = 0,
  size = 20,
): Promise<Page<Application>> {
  const params: Record<string, unknown> = { page, size }
  if (status && status !== 'TODAS') params.status = status
  const { data } = await api.get<Page<Application>>(
    `/applications/club/${clubId}`,
    { params },
  )
  return data
}

export interface Club {
  id: number
  name: string
  description: string
}

export interface RegisterPayload {
  name: string
  email: string
  clubId: number
  file: File
}

export async function getClubs(): Promise<Club[]> {
  const { data } = await api.get<Club[]>('/clubs')
  return data
}

export async function registerEnrollment(payload: RegisterPayload): Promise<void> {
  const form = new FormData()
  form.append('name', payload.name)
  form.append('email', payload.email)
  form.append('clubId', String(payload.clubId))
  form.append('file', payload.file)

  await api.post('/enrollments/register', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/** GET /api/clubs/{clubId}/requests/pending — en EnrollmentRequestController */
export async function getPendingRequests(
  clubId: number,
  page = 0,
  size = 20,
): Promise<Page<EnrollmentRequest>> {
  const { data } = await api.get<Page<EnrollmentRequest>>(
    `/clubs/${clubId}/requests/pending`,
    { params: { page, size } },
  )
  return data
}

/** PATCH /api/applications/{id}/approve */
export async function approveRequest(
  _clubId: number,
  requestId: number,
): Promise<void> {
  await api.patch(`/applications/${requestId}/approve`, {})
}

/** PATCH /api/applications/{id}/reject */
export async function rejectRequest(
  _clubId: number,
  requestId: number,
  reason: string,
): Promise<void> {
  await api.patch(`/applications/${requestId}/reject`, { reason })
}

/** GET /api/enrollments/my-requests */
export async function getMyRequests(): Promise<EnrollmentRequest[]> {
  const { data } = await api.get<EnrollmentRequest[]>('/enrollments/my-requests')
  return data
}
