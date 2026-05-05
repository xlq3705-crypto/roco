<template>
  <view class="page-item-list">
    <search-bar v-model="keyword" placeholder="搜索道具名称" @search="onSearch" />
    <filter-tabs v-model="currentCategory" :tabs="categoryTabs" @change="onCategoryChange" />

    <view class="item-grid">
      <view
        v-for="item in itemList"
        :key="item.id"
        class="item-card"
        @tap="goDetail(item.id)"
      >
        <image
          class="item-image"
          :src="item.imageUrl || '/static/images/default.png'"
          mode="aspectFit"
        />
        <text class="item-name">{{ item.name }}</text>
        <text class="item-category">{{ item.category }}</text>
      </view>
    </view>

    <view v-if="loading" class="loading"><text>加载中...</text></view>
    <view v-if="!loading && itemList.length === 0" class="empty"><text>暂无道具数据</text></view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import SearchBar from '@/components/search-bar/search-bar.vue'
import FilterTabs from '@/components/filter-tabs/filter-tabs.vue'

const keyword = ref('')
const currentCategory = ref('')
const itemList = ref<any[]>([])
const loading = ref(false)

const categoryTabs = [
  { label: '全部', value: '' },
  { label: '进化道具', value: '进化道具' },
  { label: '活动道具', value: '活动道具' },
  { label: '其他', value: '其他' }
]

onLoad(() => {
  loadItems()
})

async function loadItems() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: 1, size: 50 }
    if (currentCategory.value) params.category = currentCategory.value
    const res = await get<any>('/api/item/list', params)
    itemList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!keyword.value.trim()) {
    loadItems()
    return
  }
  loading.value = true
  try {
    const res = await get<any>('/api/item/search', { keyword: keyword.value, page: 1, size: 50 })
    itemList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function onCategoryChange() {
  loadItems()
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/item-detail/item-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page-item-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.item-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx;
}

.item-card {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  box-sizing: border-box;
}

.item-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  background: #fff;
}

.item-name {
  font-size: 24rpx;
  color: #333;
  margin-top: 8rpx;
  text-align: center;
}

.item-category {
  font-size: 20rpx;
  color: #999;
  margin-top: 4rpx;
}

.loading, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
}
</style>
