import confetti from 'canvas-confetti'

/**
 * Confetti celebration composable
 * Provides themed confetti effects for achievements and course completion
 */
export function useConfetti() {
  /** Standard celebration burst (course completion, achievement unlock) */
  function celebrate() {
    const duration = 2500
    const end = Date.now() + duration

    const frame = () => {
      confetti({
        particleCount: 3,
        angle: 60,
        spread: 55,
        origin: { x: 0, y: 0.7 },
        colors: ['#635BFF', '#818CF8', '#06B6D4', '#FFC800', '#58CC02'],
      })
      confetti({
        particleCount: 3,
        angle: 120,
        spread: 55,
        origin: { x: 1, y: 0.7 },
        colors: ['#635BFF', '#818CF8', '#06B6D4', '#FFC800', '#58CC02'],
      })

      if (Date.now() < end) {
        requestAnimationFrame(frame)
      }
    }
    frame()
  }

  /** Quick pop for smaller wins (chapter complete, check-in) */
  function pop() {
    confetti({
      particleCount: 60,
      spread: 70,
      origin: { y: 0.6 },
      colors: ['#635BFF', '#818CF8', '#58CC02', '#FFC800'],
      ticks: 150,
    })
  }

  /** Achievement-style starburst */
  function achievement() {
    const defaults = {
      spread: 360,
      ticks: 100,
      gravity: 0,
      decay: 0.94,
      startVelocity: 30,
      colors: ['#FFD700', '#FFC800', '#FF9600', '#FFE66D', '#FFA502'],
    }

    confetti({ ...defaults, particleCount: 30, scalar: 1.2, shapes: ['star'] })
    confetti({ ...defaults, particleCount: 20, scalar: 0.75, shapes: ['circle'] })

    setTimeout(() => {
      confetti({ ...defaults, particleCount: 25, scalar: 1.2, shapes: ['star'] })
      confetti({ ...defaults, particleCount: 15, scalar: 0.75, shapes: ['circle'] })
    }, 200)
  }

  return { celebrate, pop, achievement }
}
