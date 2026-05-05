<template>
  <view class="page-skill-list">
    <search-bar v-model="keyword" placeholder="搜索技能名称" @search="onSearch" />
    <filter-tabs v-model="currentAttr" :tabs="attrTabs" @change="onAttrChange" />

    <view class="skill-list">
      <view
        v-for="skill in skillList"
        :key="skill.id"
        class="skill-card"
        @tap="goDetail(skill.id)"
      >
        <view class="skill-header">
          <text class="skill-name">{{ skill.name }}</text>
          <attr-badge :attr="skill.attr" />
        </view>
        <view class="skill-info">
          <text class="skill-type">{{ skill.type }}</text>
          <text class="skill-power" v-if="skill.power">威力: {{ skill.power }}</text>
          <text class="skill-pp">PP: {{ skill.pp }}</text>
        </view>
        <text class="skill-desc" v-if="skill.description">{{ skill.description }}</text>
      </view>
    </view>

    <view v-if="loading" class="loading"><text>加载中...</text></view>
    <view v-if="!loading && skillList.length === 0" class="empty"><text>暂无技能数据</text></view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import SearchBar from '@/components/search-bar/search-bar.vue'
import FilterTabs from '@/components/filter-tabs/filter-tabs.vue'
import AttrBadge from '@/components/attr-badge/attr-badge.vue'

const keyword = ref('')
const currentAttr = ref('')
const skillList = ref<any[]>([])
const loading = ref(false)

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

onLoad(() => {
  loadSkills()
})

async function loadSkills() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: 1, size: 50 }
    if (currentAttr.value) params.attr = currentAttr.value
    const res = await get<any>('/api/skill/list', params)
    skillList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!keyword.value.trim()) {
    loadSkills()
    return
  }
  loading.value = true
  try {
    const res = await get<any>('/api/skill/search', { keyword: keyword.value, page: 1, size: 50 })
    skillList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function onAttrChange() {
  loadSkills()
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/skill-detail/skill-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page-skill-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.skill-list {
  padding: 0 24rpx;
}

.skill-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.skill-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.skill-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.skill-info {
  display: flex;
  gap: 20rpx;
  margin-top: 12rpx;
}

.skill-type, .skill-power, .skill-pp {
  font-size: 24rpx;
  color: #666;
}

.skill-desc {
  font-size: 26rpx;
  color: #999;
  margin-top: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.loading, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
}
</style>
