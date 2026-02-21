import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import { inject } from '@vercel/analytics'
import App from './App.vue'
import './style.css'

import HomeView from './views/HomeView.vue'
import WhyView from './views/WhyView.vue'
import SetupView from './views/SetupView.vue'

// Inject Vercel Analytics
inject()

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: HomeView },
        { path: '/why', component: WhyView },
        { path: '/setup', component: SetupView },
    ],
    scrollBehavior() {
        return { top: 0 }
    }
})

createApp(App).use(router).mount('#app')
