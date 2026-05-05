const BASE_URL = 'http://localhost:8080'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST'
  data?: Record<string, any>
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      success: (res: any) => {
        const data = res.data as ApiResponse<T>
        if (data.code === 200) {
          resolve(data)
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(data)
        }
      },
      fail: (err: any) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export function get<T = any>(url: string, data?: Record<string, any>): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'GET', data })
}
