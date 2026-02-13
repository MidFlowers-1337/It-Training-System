import { ref } from 'vue'

export function useAsyncRequest<T = any>() {
  const data = ref<T | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function execute(fn: () => Promise<T>) {
    loading.value = true
    error.value = null
    try {
      data.value = await fn() as any
      return data.value
    } catch (e: any) {
      error.value = e.message || '请求失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return { data, loading, error, execute }
}
