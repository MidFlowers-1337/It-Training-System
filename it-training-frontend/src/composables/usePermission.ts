import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

export function usePermission() {
  const userStore = useUserStore()

  const isAdmin = computed(() => userStore.isAdmin)
  const isInstructor = computed(() => userStore.isInstructor)
  const isStudent = computed(() => userStore.isStudent)
  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const role = computed(() => userStore.role)

  function hasRole(r: string) {
    return userStore.role === r
  }

  return { isAdmin, isInstructor, isStudent, isLoggedIn, role, hasRole }
}
