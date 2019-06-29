<template>
<div>
    <v-card>
        <v-card-title>
        <v-btn color="success" @click="addBrand">新增</v-btn>
            <v-spacer/>
            <v-text-field
                    label="请输入搜索内容"
                    append-icon="search"
                    hide-details
                    v-model="search"
            ></v-text-field>
        </v-card-title>
        <v-divider/>
        <v-data-table
                :headers="headers"
                :items="brands"
                :pagination.sync="pagination"
                :total-items="totalBrands"
                :loading="loading"
                class="elevation-1"
        >
            <template slot="items" slot-scope="props">
                <td>{{ props.item.id }}</td>
                <td class="text-xs-center">{{ props.item.name }}</td>
                <td class="text-xs-center"><img v-if="props.item.image" :src="props.item.image" width="130" height="40">
                    <span v-else>无</span></td>
                <td class="text-xs-center">{{ props.item.letter }}</td>
                <td class="text-xs-center">
                    <v-btn color="info">编辑</v-btn>
                    <v-btn color="error">删除</v-btn>
                </td>
            </template>
        </v-data-table>
    </v-card>
    <v-dialog v-model="dialog" max-width="500px" persistent>
        <brand-form @close="close"></brand-form>
    </v-dialog>
</div>
</template>
<script>
    import  BrandForm from "./BrandForm"
    export default {
        components:{
            BrandForm
        },
        name: "my-brand",
        data() {
            return {
                totalBrands: 0, // 总条数
                brands: [], // 当前页品牌数据
                loading: true, // 是否在加载中
                pagination: {}, // 分页信息
                search:'',
                dialog:false,
                headers: [
                    {text: 'id', align: 'center', value: 'id'},
                    {text: '名称', align: 'center', sortable: false, value: 'name'},
                    {text: 'LOGO', align: 'center', sortable: false, value: 'image'},
                    {text: '首字母', align: 'center', value: 'letter', sortable: true},
                    {text:'操作',align:'center',value:'op',sortable:false}
                ]
            }
        },
        mounted(){ // 渲染后执行
            // 查询数据
            this.getDataFromServer();
        },
        methods:{
            getDataFromServer() { // 从服务的加载数据的方法。
               this.$http.get("/item/brand/page",{
                   params:{
                       page: this.pagination.page,
                       rows:this.pagination.rowsPerPage,
                       sortBy: this.pagination.sortBy,
                       desc:this.pagination.descending,
                       search:this.search
                   }

               }).then(({data})=>{
                    this.brands=data.items;
                    this.totalBrands=data.total;
                   this.loading=false;
                }).catch(resp=>{
                    console.log("查询失败！！");
               })

            },
            addBrand(){
                this.dialog=true;
            },
            close(){
                this.dialog=false;
            }
        },
        watch:{
            pagination:{
                deep:true,
                handler(){
                    this.getDataFromServer();
                }
            },
            search:{
                handler(){
                    this.getDataFromServer();
                }
            }
        }
    }
</script>