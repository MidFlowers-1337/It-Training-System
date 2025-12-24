import { defineStore } from 'pinia'
import { getPublishedCourses, getCourseById, getCourseChapters } from '@/api/course'

/**
 * Course data cache store
 * Reduces API calls by caching course data
 */
export const useCourseStore = defineStore('course', {
  state: () => ({
    // Course list cache
    publishedCourses: [],
    coursesLoaded: false,
    coursesLoadedAt: null,

    // Course detail cache (keyed by course ID)
    courseDetails: {},

    // Course chapters cache (keyed by course ID)
    courseChapters: {},

    // Loading states
    loading: false,
    error: null
  }),

  getters: {
    /**
     * Check if courses cache is stale (older than 5 minutes)
     */
    isCacheStale: (state) => {
      if (!state.coursesLoadedAt) return true
      const fiveMinutes = 5 * 60 * 1000
      return Date.now() - state.coursesLoadedAt > fiveMinutes
    },

    /**
     * Get course by ID from cache
     */
    getCourseFromCache: (state) => (id) => {
      return state.courseDetails[id] || null
    },

    /**
     * Get chapters by course ID from cache
     */
    getChaptersFromCache: (state) => (courseId) => {
      return state.courseChapters[courseId] || null
    },

    /**
     * Filter courses by category
     */
    getCoursesByCategory: (state) => (category) => {
      return state.publishedCourses.filter(c => c.category === category)
    }
  },

  actions: {
    /**
     * Fetch published courses (with cache)
     */
    async fetchPublishedCourses(forceRefresh = false) {
      // Return cached data if fresh
      if (!forceRefresh && this.coursesLoaded && !this.isCacheStale) {
        return this.publishedCourses
      }

      this.loading = true
      this.error = null

      try {
        const res = await getPublishedCourses()
        this.publishedCourses = res.data || []
        this.coursesLoaded = true
        this.coursesLoadedAt = Date.now()
        return this.publishedCourses
      } catch (err) {
        this.error = err.message || 'Failed to fetch courses'
        throw err
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch course detail (with cache)
     */
    async fetchCourseDetail(id, forceRefresh = false) {
      // Return cached data if exists
      if (!forceRefresh && this.courseDetails[id]) {
        return this.courseDetails[id]
      }

      this.loading = true
      this.error = null

      try {
        const res = await getCourseById(id)
        this.courseDetails[id] = res.data
        return res.data
      } catch (err) {
        this.error = err.message || 'Failed to fetch course detail'
        throw err
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch course chapters (with cache)
     */
    async fetchCourseChapters(courseId, forceRefresh = false) {
      // Return cached data if exists
      if (!forceRefresh && this.courseChapters[courseId]) {
        return this.courseChapters[courseId]
      }

      this.loading = true
      this.error = null

      try {
        const res = await getCourseChapters(courseId)
        this.courseChapters[courseId] = res.data || []
        return this.courseChapters[courseId]
      } catch (err) {
        this.error = err.message || 'Failed to fetch chapters'
        throw err
      } finally {
        this.loading = false
      }
    },

    /**
     * Clear all cache
     */
    clearCache() {
      this.publishedCourses = []
      this.coursesLoaded = false
      this.coursesLoadedAt = null
      this.courseDetails = {}
      this.courseChapters = {}
    },

    /**
     * Clear specific course cache
     */
    clearCourseCache(courseId) {
      delete this.courseDetails[courseId]
      delete this.courseChapters[courseId]
    }
  }
})
