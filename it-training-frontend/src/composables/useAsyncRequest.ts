import { ref, type Ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * Async request state interface
 */
interface AsyncState<T> {
  data: Ref<T | null>
  loading: Ref<boolean>
  error: Ref<string | null>
}

/**
 * Options for useAsyncRequest
 */
interface UseAsyncRequestOptions {
  // Show success message
  successMessage?: string
  // Show error message (default: true)
  showError?: boolean
  // Custom error message
  errorMessage?: string
  // Callback on success
  onSuccess?: (data: any) => void
  // Callback on error
  onError?: (error: any) => void
}

/**
 * Unified async request composable
 * Provides consistent loading, error handling and user feedback
 *
 * @example
 * ```ts
 * const { execute, loading, error, data } = useAsyncRequest()
 *
 * // Execute async request
 * await execute(
 *   () => fetchUserProfile(userId),
 *   { successMessage: '加载成功' }
 * )
 *
 * // Or with immediate execution
 * const { loading, data, execute } = useAsyncRequest()
 * onMounted(() => execute(() => fetchCourses()))
 * ```
 */
export function useAsyncRequest<T = any>(): AsyncState<T> & {
  execute: (fn: () => Promise<any>, options?: UseAsyncRequestOptions) => Promise<T | null>
  reset: () => void
} {
  const data = ref<T | null>(null) as Ref<T | null>
  const loading = ref(false)
  const error = ref<string | null>(null)

  /**
   * Execute async function with unified error handling
   */
  async function execute(
    fn: () => Promise<any>,
    options: UseAsyncRequestOptions = {}
  ): Promise<T | null> {
    const {
      successMessage,
      showError = true,
      errorMessage,
      onSuccess,
      onError
    } = options

    loading.value = true
    error.value = null

    try {
      const result = await fn()
      data.value = result?.data ?? result

      if (successMessage) {
        ElMessage.success(successMessage)
      }

      onSuccess?.(data.value)
      return data.value
    } catch (err: any) {
      const message = errorMessage || err?.message || '请求失败，请稍后重试'
      error.value = message

      if (showError) {
        // Avoid duplicate error messages from request interceptor
        // Only show if error doesn't have response (not handled by interceptor)
        if (!err?.response) {
          ElMessage.error(message)
        }
      }

      onError?.(err)
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * Reset state
   */
  function reset() {
    data.value = null
    loading.value = false
    error.value = null
  }

  return {
    data,
    loading,
    error,
    execute,
    reset
  }
}

/**
 * Simple loading state composable
 * For cases where you just need loading state
 *
 * @example
 * ```ts
 * const { loading, withLoading } = useLoading()
 *
 * await withLoading(async () => {
 *   await saveData()
 * })
 * ```
 */
export function useLoading() {
  const loading = ref(false)

  async function withLoading<T>(fn: () => Promise<T>): Promise<T> {
    loading.value = true
    try {
      return await fn()
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    withLoading
  }
}

/**
 * Debounced async request composable
 * Prevents rapid repeated requests
 *
 * @example
 * ```ts
 * const { execute, loading } = useDebouncedRequest(300)
 *
 * // Only last request within 300ms will execute
 * execute(() => searchCourses(keyword))
 * ```
 */
export function useDebouncedRequest<T = any>(delay = 300) {
  const { data, loading, error, execute: baseExecute, reset } = useAsyncRequest<T>()
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  function execute(
    fn: () => Promise<any>,
    options?: UseAsyncRequestOptions
  ): Promise<T | null> {
    return new Promise((resolve) => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }

      timeoutId = setTimeout(async () => {
        const result = await baseExecute(fn, options)
        resolve(result)
      }, delay)
    })
  }

  function cancel() {
    if (timeoutId) {
      clearTimeout(timeoutId)
      timeoutId = null
    }
  }

  return {
    data,
    loading,
    error,
    execute,
    reset,
    cancel
  }
}
