<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <img src="@/assets/logo.svg" alt="logo" class="logo-img" />
        <span v-show="!isCollapse" class="logo-text">图鉴管理系统</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        class="menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-sub-menu index="data">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>数据管理</span>
          </template>
          <el-menu-item index="/pet">
            <el-icon><ElementPlus /></el-icon>
            <template #title>宠物管理</template>
          </el-menu-item>
          <el-menu-item index="/skill">
            <el-icon><MagicStick /></el-icon>
            <template #title>技能管理</template>
          </el-menu-item>
          <el-menu-item index="/item">
            <el-icon><Box /></el-icon>
            <template #title>道具管理</template>
          </el-menu-item>
          <el-menu-item index="/equipment">
            <el-icon><Suitcase /></el-icon>
            <template #title>装备管理</template>
          </el-menu-item>
          <el-menu-item index="/attribute">
            <el-icon><Connection /></el-icon>
            <template #title>属性克制</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item v-if="adminStore.isSuper()" index="/admin">
            <el-icon><UserFilled /></el-icon>
            <template #title>管理员管理</template>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="log">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>日志管理</span>
          </template>
          <el-menu-item index="/log/login">
            <el-icon><Tickets /></el-icon>
            <template #title>登录日志</template>
          </el-menu-item>
          <el-menu-item index="/log/operation">
            <el-icon><Notebook /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/import-export">
          <el-icon><Download /></el-icon>
          <template #title>导入导出</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            @click="isCollapse = !isCollapse"
          >
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-info">
              <el-icon><UserFilled /></el-icon>
              <span class="admin-name">{{ adminStore.adminInfo?.nickname || '管理员' }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()
const isCollapse = ref(false)

function handleCommand(command: string) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      adminStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    background-color: #263445;

    .logo-img {
      width: 32px;
      height: 32px;
    }

    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      margin-left: 10px;
      white-space: nowrap;
    }
  }

  .menu {
    border-right: none;
    height: calc(100vh - 60px);
    overflow-y: auto;

    &:not(.el-menu--collapse) {
      width: 220px;
    }
  }
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      color: #333;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    .admin-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #333;
      font-size: 14px;

      .admin-name {
        margin: 0 4px;
      }
    }
  }
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
