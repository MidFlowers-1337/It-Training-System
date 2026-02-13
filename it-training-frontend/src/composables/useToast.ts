import { reactive } from 'vue'

/* ── Types ── */
export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface ToastItem {
  id: number
  type: ToastType
  message: string
  duration: number
  visible: boolean
}

interface ToastOptions {
  duration?: number
}

/* ── Reactive singleton queue ── */
const queue = reactive<ToastItem[]>([])
let nextId = 0

function addToast(type: ToastType, message: string, options?: ToastOptions) {
  const id = nextId++
  const duration = options?.duration ?? 3000
  const item: ToastItem = { id, type, message, duration, visible: true }
  queue.push(item)

  if (duration > 0) {
    setTimeout(() => removeToast(id), duration)
  }
}

function removeToast(id: number) {
  const idx = queue.findIndex((t) => t.id === id)
  if (idx !== -1) {
    const item = queue[idx]
    if (item) item.visible = false
    // Allow exit animation to finish before removing from DOM
    setTimeout(() => {
      const removeIdx = queue.findIndex((t) => t.id === id)
      if (removeIdx !== -1) queue.splice(removeIdx, 1)
    }, 300)
  }
}

/* ── Public API (singleton — works everywhere, including request.ts) ── */
export const toast = {
  success: (message: string, options?: ToastOptions) => addToast('success', message, options),
  error: (message: string, options?: ToastOptions) => addToast('error', message, options),
  warning: (message: string, options?: ToastOptions) => addToast('warning', message, options),
  info: (message: string, options?: ToastOptions) => addToast('info', message, options),
}

/* ── Composable (for components that need the queue) ── */
export function useToast() {
  return {
    queue,
    removeToast,
    ...toast,
  }
}
