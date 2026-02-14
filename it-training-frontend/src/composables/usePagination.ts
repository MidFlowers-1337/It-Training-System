import { ref, computed } from 'vue'

export function usePagination(defaultSize = 12) {
  const currentPage = ref(1)
  const pageSize = ref(defaultSize)
  const total = ref(0)

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  function setTotal(n: number) {
    total.value = n
  }

  function goToPage(page: number) {
    currentPage.value = page
  }

  function reset() {
    currentPage.value = 1
  }

  const params = computed(() => ({
    page: currentPage.value,
    size: pageSize.value,
  }))

  return {
    currentPage, pageSize, total, totalPages,
    setTotal, goToPage, reset, params,
  }
}
