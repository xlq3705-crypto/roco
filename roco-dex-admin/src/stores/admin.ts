import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface AdminInfo {
  id: number
  username: string
  nickname: string
  role: string
}

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<AdminInfo | null>(
    JSON.parse(localStorage.getItem('admin_info') || 'null')
  )

  async function login(username: string, password: string) {
    const res: any = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    adminInfo.value = { id: res.data.id, username: res.data.username, nickname: res.data.nickname, role: res.data.role }
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_info', JSON.stringify(adminInfo.value))
    return res
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  async function getCurrentAdmin() {
    const res: any = await request.get('/auth/me')
    adminInfo.value = { id: res.data.id, username: res.data.username, nickname: res.data.nickname, role: res.data.role }
    localStorage.setItem('admin_info', JSON.stringify(adminInfo.value))
    return res
  }

  function isSuper() {
    return adminInfo.value?.role === 'super'
  }

  return {
    token,
    adminInfo,
    login,
    logout,
    getCurrentAdmin,
    isSuper
  }
})
