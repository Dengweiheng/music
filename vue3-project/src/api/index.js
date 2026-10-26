import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const request = axios.create({
    baseURL: '/api', // Proxied to http://localhost:8080
    timeout: 5000
})

// Request interceptor
request.interceptors.request.use(config => {
    const userStore = useUserStore()
    if (userStore.token) {
        config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
}, error => {
    return Promise.reject(error)
})

// Response interceptor
request.interceptors.response.use(response => {
    let res = response.data;
    // If response is a blob or string, just return
    if (typeof res === 'string') {
        res = res ? JSON.parse(res) : res
    }

    // Custom error handling
    if (res.code && res.code !== 200) {
        ElMessage.error(res.message || 'Error')
        return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
}, error => {
    console.log('err' + error)
    let msg = '服务器异常'
    if (error.response?.status === 401 || error.response?.status === 403) {
        msg = '请先登录'
        const userStore = useUserStore()
        userStore.clearToken()
        window.location.href = '/login'
    }
    ElMessage.error(msg)
    return Promise.reject(error)
})

export default request
