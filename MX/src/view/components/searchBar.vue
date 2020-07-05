<template>
  <div class="box_row colCenter rowRight pageFindSty" style="border: none;">
    <div v-for="r in columns" v-if="r.searchKey || r.searchType" style="float: left">
      <Icon :type="r.icon" size="34"/>
      <Input v-if="!r.searchType || r.searchType === 'text'" v-model="parent.param[r.searchKey]"
             :placeholder="'请输入'+r.title" style="width: 150px"></Input>
      <DatePicker v-else-if="r.searchType == 'daterange'" v-model="dateRange[r.key]"
                  @on-change="param[r.key+'InRange'] = parent.util.dateRangeChange(dateRange[r.key])"
                  @on-ok="parent.util.getPageData(parent)" confirm format="yyyy-MM-dd"
                  type="daterange" :placeholder="'请输入'+r.title" split-panels style="width: 200px"></DatePicker>
      <Select v-else-if="r.searchType == 'dict'" filterable clearable v-model="param[r.key]" :placeholder="r.title"
              style="width: 200px">
        <Option v-for='(item,index) in parent.dictUtil.getByCode(parent,r.dict)'
                v-if="r.excludeDict == null || r.excludeDict.indexOf(item.key) < 0" :value="item.key" :key="item.key">
          {{item.val}}
        </Option>
      </Select>
    </div>

    <Button v-if="showSearchButton" type="primary" @click="parent.util.getPageData(parent)" style="margin: 0">
      <Icon type="md-search"></Icon>
      <!--查询-->
    </Button>

    <Button v-if="showDownLoadButton" type="primary" @click="exportExcel" style="margin: 0;margin-left: 10px">
      <Icon type="ios-cloud-download" />
      <!--导出-->
    </Button>

    <!--<Col span="5" offset="1">-->
      <!--<Upload-->
        <!--ref="upload"-->
        <!--type="drag">-->
        <!--<div style="padding: 20px 0">-->
          <!--<Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>-->
        <!--</div>-->
      <!--</Upload>-->
    <!--</Col>-->


    <Button v-if="showCreateButton" type="primary" @click="parent.util.add(parent)">
      <Icon type="md-add"></Icon>
    </Button>
    <Button style="margin-right: 0" v-for="(item,index) in buttons" key="index" :key="index" :type="item.type ? item.type : 'primary'" @click="$emit(item.click)">
      <Icon v-if="item.icon" :type="item.icon"></Icon>
      {{item.title}}
    </Button>
  </div>
</template>

<script>
  export default {
    name: "searchBar",

    props: {
      parent: {
        type: Object,
        default: function () {
          return {};
        }
      },
      showSearchButton: {
        type: Boolean,
        default: true
      },
      showDownLoadButton: {
        type: Boolean,
        default: true
      },
      showCreateButton: {
        type: Boolean,
        default: true
      },
      labelWith: {
        type: Number,
        default: 100
      },
      inputWith: {
        type: Number,
        default: 100
      },
        buttons:{
          type:Array,
            default:function () {
                return [];
            }
        }
    },
    data() {
      return {
        labelStyle: '',
        inputStyle: 'width:100px;',
        dateRange: {},
        columns: [],
          param: {},
      }
    },
    created() {

      if (this.parent.tableColumns) {
        this.columns = JSON.parse(JSON.stringify(this.parent.tableColumns));
        if (this.parent.searchParams){
            for(let r of this.parent.searchParams){
                this.columns.push(r);
            }
        }
      }
      if (this.parent.dateRange) {
        this.dateRange = this.parent.dateRange;
        for (let k in this.dateRange){
            let dr = this.dateRange[k];
            if (dr.showToday){
                let now = new Date();
                let today = now.format("yyyy-MM-dd")
                this.dateRange[k] = [now,now]
                let t = this.parent.util.dateRangeChange(this.dateRange[k])
            }
        }
      }
      if (this.parent.param) {
        this.param = this.parent.param;
      }
    },
    methods:{
      exportExcel(){
        this.$emit('exportExcel')
      }
    }
  }
</script>

<style scoped>

</style>
