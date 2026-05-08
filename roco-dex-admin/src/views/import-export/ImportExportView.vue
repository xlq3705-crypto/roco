<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Download /></el-icon>
              <span>数据导出</span>
            </div>
          </template>
          <el-form label-width="80px">
            <el-form-item label="模块">
              <el-select v-model="exportModule" placeholder="请选择模块" style="width: 100%">
                <el-option label="宠物" value="pet" />
                <el-option label="技能" value="skill" />
                <el-option label="道具" value="item" />
                <el-option label="装备" value="equipment" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="exportLoading"
                @click="handleExport"
                style="width: 100%"
              >
                <el-icon><Download /></el-icon>导出Excel
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Upload /></el-icon>
              <span>数据导入</span>
            </div>
          </template>
          <el-form label-width="80px">
            <el-form-item label="模块">
              <el-select v-model="importModule" placeholder="请选择模块" style="width: 100%">
                <el-option label="宠物" value="pet" />
                <el-option label="技能" value="skill" />
                <el-option label="道具" value="item" />
                <el-option label="装备" value="equipment" />
              </el-select>
            </el-form-item>
            <el-form-item label="文件">
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :limit="1"
                accept=".xlsx,.xls"
                :on-change="handleFileChange"
                :on-exceed="handleExceed"
                drag
              >
                <el-icon class="el-icon--upload"><Upload /></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处，或<em>点击上传</em>
                </div>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 .xlsx / .xls 文件
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="importLoading"
                :disabled="!importFile"
                @click="handleImport"
                style="width: 100%"
              >
                <el-icon><Upload /></el-icon>开始导入
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="importResult" shadow="never" style="margin-top: 20px">
      <template #header>
        <span>导入结果</span>
      </template>
      <el-result
        :icon="importResult.success ? 'success' : 'warning'"
        :title="importResult.success ? '导入完成' : '导入部分失败'"
      >
        <template #sub-title>
          <p>成功: {{ importResult.successCount }} 条</p>
          <p>失败: {{ importResult.failCount }} 条</p>
          <p v-if="importResult.failReasons && importResult.failReasons.length">
            失败原因:
          </p>
          <ul v-if="importResult.failReasons && importResult.failReasons.length">
            <li v-for="(reason, index) in importResult.failReasons" :key="index">
              {{ reason }}
            </li>
          </ul>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { UploadFile, UploadInstance } from 'element-plus'

const exportModule = ref('pet')
const importModule = ref('pet')
const exportLoading = ref(false)
const importLoading = ref(false)
const importFile = ref<File | null>(null)
const uploadRef = ref<UploadInstance>()
const importResult = ref<{
  success: boolean
  successCount: number
  failCount: number
  failReasons: string[]
} | null>(null)

async function handleExport() {
  if (!exportModule.value) {
    ElMessage.warning('请选择模块')
    return
  }

  exportLoading.value = true
  try {
    const response = await request.get(`/import-export/export/${exportModule.value}`, {
      responseType: 'blob'
    })
    const blob = new Blob([response as any], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${exportModule.value}_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

function handleFileChange(file: UploadFile) {
  importFile.value = file.raw || null
}

function handleExceed() {
  ElMessage.warning('只能上传一个文件，请先移除已选文件')
}

async function handleImport() {
  if (!importModule.value) {
    ElMessage.warning('请选择模块')
    return
  }
  if (!importFile.value) {
    ElMessage.warning('请选择文件')
    return
  }

  importLoading.value = true
  importResult.value = null
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    const res: any = await request.post(`/import-export/import/${importModule.value}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    importResult.value = res.data
    ElMessage.success('导入完成')
    uploadRef.value?.clearFiles()
    importFile.value = null
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.page-container {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;

    .el-icon {
      font-size: 18px;
    }
  }
}
</style>
