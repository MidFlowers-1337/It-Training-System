import request from './request'

export interface AiRecommendRequest {
  message: string
  context?: string
}

export const aiApi = {
  recommend: (data: AiRecommendRequest) => request.post('/v1/ai/recommend', data),
  health: () => request.get('/v1/ai/test/health'),
  chat: (data: { message: string }) => request.post('/v1/ai/test/chat', data),
  chatWithSystem: (data: { message: string; systemPrompt: string }) => request.post('/v1/ai/test/chat-with-system', data),
  chatJson: (data: any) => request.post('/v1/ai/test/chat-json', data),
  chatWithOptions: (data: any) => request.post('/v1/ai/test/chat-with-options', data),
}
