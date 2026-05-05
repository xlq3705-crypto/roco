<template>
  <view class="page-pet-list">
    <!-- 搜索栏 -->
    <search-bar v-model="keyword" placeholder="搜索宠物名称或编号" @search="onSearch" />

    <!-- 属性筛选 -->
    <filter-tabs v-model="currentAttr" :tabs="attrTabs" @change="onAttrChange" />

    <!-- 宠物列表 -->
    <view class="pet-grid">
      <view
        v-for="pet in petList"
        :key="pet.id"
        class="pet-card"
        @tap="goDetail(pet.id)"
      >
        <image
          class="pet-image"
          :src="pet.imageUrl || '/static/images/default.png'"
          mode="aspectFit"
        />
        <text class="pet-name">{{ pet.name }}</text>
        <view class="pet-attrs">
          <attr-badge :attr="pet.mainAttr" />
          <attr-badge v-if="pet.subAttr" :attr="pet.subAttr" />
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-if="!loading && petList.length === 0" class="empty">
      <text>暂无宠物数据</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import SearchBar from '@/components/search-bar/search-bar.vue'
import FilterTabs from '@/components/filter-tabs/filter-tabs.vue'
import AttrBadge from '@/components/attr-badge/attr-badge.vue'

const keyword = ref('')
const currentAttr = ref('')
const petList = ref<any[]>([])
const loading = ref(false)
const page = ref(1)

const attrTabs = [
  { label: '全部', value: '' },
  { label: '普通', value: '普通' },
  { label: '火', value: '火' },
  { label: '水', value: '水' },
  { label: '草', value: '草' },
  { label: '电', value: '电' },
  { label: '冰', value: '冰' },
  { label: '翼', value: '翼' },
  { label: '土', value: '土' },
  { label: '萌', value: '萌' },
  { label: '虫', value: '虫' },
  { label: '幽灵', value: '幽灵' },
  { label: '龙', value: '龙' },
  { label: '恶魔', value: '恶魔' },
  { label: '机械', value: '机械' },
  { label: '光', value: '光' }
]

onLoad((options: any) => {
  loadPets()
})

async function loadPets() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: page.value, size: 20 }
    if (currentAttr.value) params.attr = currentAttr.value
    const res = await get<any>('/api/pet/list', params)
    petList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!keyword.value.trim()) {
    loadPets()
    return
  }
  loading.value = true
  try {
    const res = await get<any>('/api/pet/search', { keyword: keyword.value, page: 1, size: 50 })
    petList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function onAttrChange() {
  page.value = 1
  loadPets()
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/pet-detail/pet-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page-pet-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.pet-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx;
}

.pet-card {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  box-sizing: border-box;
}

.pet-image {
  width: 110rpx;
  height: 110rpx;
  border-radius: 16rpx;
  background: #fff;
}

.pet-name {
  font-size: 24rpx;
  color: #333;
  margin-top: 8rpx;
}

.pet-attrs {
  display: flex;
  gap: 6rpx;
  margin-top: 6rpx;
}

.loading, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
}
</style>
