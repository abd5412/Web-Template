<template>
    <div class="user-management">
        <!-- 搜索和操作栏 -->
        <div class="operation-bar">
            <el-input v-model="searchName" placeholder="搜索用户" style="width: 300px" clearable
                @keyup.enter="handleSearch">
                <template #append>
                    <el-button :icon="Search" @click="handleSearch" />
                </template>
            </el-input>

            <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
        </div>

        <!-- 用户表格 -->
        <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
            <el-table-column label="ID" width="80" type="index" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="authorities" label="角色">
                <template #default="{ row }">
                    <el-tag style="margin-right: 5px;" v-for="(authority, index) in row.authorities" :key="index"
                        :type="roleTagType(authority)">
                        {{ authority.label }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="cdTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button size="small" @click="handleEdit(row)">编辑</el-button>
                    <el-button size="small" @click="handleEdit(row)">下线</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(row.id)">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[5, 10, 20]"
                :total="total" layout="total, sizes, prev, pager, next, jumper" />
        </div>

        <!-- 用户表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="formTitle" width="500px">
            <el-form ref="userForm" :model="formData" :rules="formRules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="formData.username" :disabled="!isAdd" />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="formData.email" />
                </el-form-item>

                <el-form-item label="手机号" prop="tel">
                    <el-input v-model="formData.tel" />
                </el-form-item>

                <el-form-item label="角色" prop="role">
                    <el-select v-model="formData.authorities" multiple placeholder="请选择角色" style="width: 240px">
                        <el-option v-for="(item, index) in roleOptions" :key="item.authorityName" :label="item.label"
                            :value="item.authorityName" />
                    </el-select>
                </el-form-item>

                <el-form-item label="密码" prop="password" >
                    <el-input v-model="formData.password" type="password" />
                </el-form-item>
            </el-form>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确认</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userPage } from '../../api/api'

// 模拟数据
// const mockUsers = Array.from({ length: 10 }, (_, i) => ({
//     id: i + 1,
//     username: `user${i + 1}`,
//     email: `user${i + 1}@example.com`,
//     role: i % 3 === 0 ? 'admin' : 'user',
//     createTime: new Date().toLocaleString()
// }))


// 响应式数据
const searchName = ref('')
const current = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const formData = reactive({
    id: null,
    username: '',
    email: '',
    role: '',
    password: ''
})
const isAdd = ref(true)
const userForm = ref(null)
const tableData = ref([])

// 角色选项
const roleOptions = reactive([
    {
        authorityName: 'ROLE_USER',
        label: '用户',
    },
    {
        authorityName: 'ROLE_ADMIN',
        label: '管理员',
    },
    {
        authorityName: 'system:dept:list',
        label: '其他',
    },
])

const validateRole = (rule, value, callback) => {
    if (value && value.length > 0) {
        callback();
    } else {
        callback(new Error('请选择角色'));
    }
};

// 表单验证规则
const rules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    role: [
        { required: true, validator: validateRole, trigger: 'change' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ],
    tel: [
        { required: true, message: '请输入电话', trigger: 'blur' }
    ]
});






// 计算属性
// const filteredUsers = computed(() => {
//     return mockUsers
//         .filter(user =>
//             user.username.includes(searchKeyword.value) ||
//             user.email.includes(searchKeyword.value)
//                 .slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
// })

const formTitle = computed(() => isAdd.value ? '新增用户' : '编辑用户')

// 方法
const roleTagType = (authority) => {
    switch (authority.authorityName) {
        case 'ROLE_ADMIN':
            return 'success';
        case 'ROLE_USER':
            return 'info';
        default:
            return 'warning';
    }
};


const handleAdd = () => {
    isAdd.value = true
    resetForm()
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isAdd.value = false
    Object.assign(formData, row)
    formData.authorities = row.authorities.map(item => item.authorityName)
    dialogVisible.value = true
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确认删除该用户？', '警告', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        // const index = mockUsers.findIndex(user => user.id === id)
        // mockUsers.splice(index, 1)
        // total.value--
        ElMessage.success('删除成功')
    })
}

const submitForm = async () => {
    try {
        await userForm.value.validate()

        if (isAdd.value) {
            // mockUsers.unshift({
            //     ...formData,
            //     id: mockUsers.length + 1,
            //     createTime: new Date().toLocaleString()
            // })
            // total.value++
        } else {
            // const index = mockUsers.findIndex(user => user.id === formData.id)
            // mockUsers.splice(index, 1, formData)
        }

        dialogVisible.value = false
        ElMessage.success('操作成功')
    } catch (error) {
        console.log('表单验证失败:', error)
    }
}

const resetForm = () => {
    formData.id = null
    formData.username = ''
    formData.email = ''
    formData.authorities = []
    formData.password = ''
    formData.tel = ''
}

const handleSearch = () => {
    loading.value = true
    let param = {
        current: current.value,
        size: size.value,
        username: searchName.value
    }
    userPage(param).then(res => {
        tableData.value = res.data.records
        total.value = res.data.total
        loading.value = false
    }).catch(err => {
        console.log(err)
        loading.value = false
    })
}

// 生命周期
onMounted(() => {
    handleSearch()
    // setTimeout(() => {
    //     loading.value = false
    // }, 500)
})
</script>

<style scoped>
.user-management {
    padding: 20px;
}

.operation-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>