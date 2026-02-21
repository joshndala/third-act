import { ref, onMounted } from 'vue'

export function usePlatformDetection() {
    const isMac = ref(true) // Default to true to avoid layout shift for Mac users
    const isMobile = ref(false)
    const isSupported = ref(true)

    onMounted(() => {
        const ua = window.navigator.userAgent.toLowerCase()

        // Simple platform checks
        const isMacPlatform = /mac|iphone|ipad|ipod/.test(ua)
        const isWindows = /win/.test(ua)
        const isActuallyMobile = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/.test(ua)

        isMac.value = isMacPlatform && !isActuallyMobile
        isMobile.value = isActuallyMobile

        // Supported if it's a desktop Mac
        isSupported.value = isMac.value
    })

    return {
        isMac,
        isMobile,
        isSupported
    }
}
