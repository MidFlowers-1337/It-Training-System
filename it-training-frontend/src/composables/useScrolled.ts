import { ref, onMounted, onUnmounted } from 'vue'

/**
 * Detects whether the page has been scrolled past 0.
 * Used to add shadow/border to sticky headers on scroll.
 */
export function useScrolled() {
  const scrolled = ref(false)

  function onScroll() {
    scrolled.value = window.scrollY > 0
  }

  onMounted(() => {
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
  })

  onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
  })

  return { scrolled }
}
