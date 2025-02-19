<template>
    <div class="dashboard-container">
        <!-- 顶部导航 -->
        <header class="header">
            <div class="logo">管理平台</div>
            <div class="user-info">
                <el-avatar :size="30" src="https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg" />

                <el-dropdown>
                    <span class="el-dropdown-link">{{ username }}</span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                            <el-dropdown-item @click="resetPwd">重置密码</el-dropdown-item>
                            <el-dropdown-item divided>Action 5</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </header>

        <div class="main-content">
            <!-- 侧边菜单 -->
            <aside class="sidebar">
                <el-menu :default-active="indexShow" class="el-menu-vertical-demo" @select="handleMenuSelect">
                    <el-menu-item index="1">
                        <el-icon><icon-menu /></el-icon>
                        <span>仪表盘</span>
                    </el-menu-item>
                    <el-sub-menu index="2">
                        <template #title>
                            <el-icon>
                                <document />
                            </el-icon>
                            <span>用户管理</span>
                        </template>
                        <el-menu-item index="2-1">用户列表</el-menu-item>
                        <el-menu-item index="2-2">用户权限</el-menu-item>

                    </el-sub-menu>
                    <el-sub-menu index="3">
                        <template #title>
                            <el-icon>
                                <setting />
                            </el-icon>
                            <span>系统设置</span>
                        </template>
                        <el-menu-item index="3-1">系统配置</el-menu-item>
                        <el-menu-item index="3-2">日志管理</el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </aside>

            <!-- 主要内容 -->
            <main class="content">
                <Index v-if="indexShow == '1'"></Index>
                <UserManagement v-if="indexShow == '2-1'"></UserManagement>
                <!-- <UserPermission v-if="indexShow === '2-2'"></UserPermission>
                <SystemConfig v-if="indexShow === '3-1'"></SystemConfig>
                <LogManagement v-if="indexShow === '3-2'"></LogManagement> -->
            </main>

            <el-dialog v-model="dialogFormVisible" title="重置密码" @close="resetPwdDiaClose">
                <el-form :model="form" ref="formRef" label-position="right" label-width="80px" :rules="rules">
                    <el-form-item label="原密码" prop="oldPwd">
                        <el-input v-model="form.oldPwd" autocomplete="off" type="password" />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPwd">
                        <el-input v-model="form.newPwd" autocomplete="off" type="password" />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPwd">
                        <el-input v-model="form.confirmPwd" autocomplete="off" type="password" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">关闭</el-button>
                        <el-button type="primary" @click="resetPwdDiaSub">
                            确认
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import Index from './page/index/index.vue'
import UserManagement from './user/userManagement.vue'
// import UserPermission from './user/userPermission.vue'
// import SystemConfig from './system/systemConfig.vue'
// import LogManagement from './system/logManagement.vue'
import {
    Menu as IconMenu,
    Document,
    Setting
} from '@element-plus/icons-vue'
import { getUserInfo, logoutApi, resetPwdApi } from '../api/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '../router'

const username = ref('')
const indexShow = ref('1')
const dialogFormVisible = ref(false)

let form = reactive({
    oldPwd: '',
    newPwd: '',
    confirmPwd: ''
})

// 自定义验证器函数
const validatePwdConfirm = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('确认密码不能为空'));
    } else if (value !== form.newPwd) {
        callback(new Error('两次输入的密码不一致'));
    } else {
        callback();
    }
}
const rules = reactive({
    oldPwd: [
        { required: true, message: '旧密码不能为空', trigger: 'blur' }
    ],
    newPwd: [
        { required: true, message: '新密码不能为空', trigger: 'blur' },
        // 可以添加更多规则，比如密码强度等
    ],
    confirmPwd: [
        { required: true, message: '确认密码不能为空', trigger: 'blur' },
        { validator: validatePwdConfirm, trigger: 'blur' }
    ]
});

const handleMenuSelect = (even) => {
    indexShow.value = even
}

onMounted(() => {
    getUserInfo().then(res => {
        localStorage.setItem('userInfo', res.data.user)
        username.value = res.data.username
        console.log(username)
    })
})

const logout = () => {
    logoutApi().then(res => {
        if (res.code == 200) {
            ElMessage.success("退出登录")
            router.push("/login")
            return
        } else {
            ElMessage.error(res.message)
        }
    })
}

const resetPwd = () => {
    dialogFormVisible.value = true
}

const resetPwdDiaClose = () => {
    form = reactive({
        oldPwd: '',
        newPwd: '',
        confirmPwd: ''
    })
}

let formRef = ref()
const resetPwdDiaSub = async () => {
    await formRef.value.validate()
    confirmBox()
}

const confirmBox = () => {
    ElMessageBox.confirm(
        '是否要重置密码?',
        '注意',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            resetPwdApi(form)
                .then(res => {
                    dialogFormVisible.value = false
                    console.log(res)
                    if (res.code === 200) {
                        ElMessage({
                            type: 'success',
                            message: '密码重置成功',
                        })
                        localStorage.removeItem('token')
                        router.push('/login')
                        return
                    } else {
                        ElMessage({
                            type: 'error',
                            message: res.msg,
                        })
                    }
                })
        })
        .catch(() => {
            // ElMessage({
            //     type: 'info',
            //     message: 'Delete canceled',
            // })
        })
}
</script>

<style scoped>
.dashboard-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
}

.header {
    height: 60px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.main-content {
    flex: 1;
    display: flex;
    overflow: hidden;
}

.sidebar {
    width: 200px;
    background-color: #fff;
    border-right: 1px solid #e6e6e6;
}

.content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}

::v-deep .el-dialog {
    --el-dialog-width: 29%
}
</style>