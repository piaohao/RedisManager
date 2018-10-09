package org.piaohao.redisManager.table;

import cn.hutool.core.util.PageUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableData<T> {
    private Integer currentPage;
    private Integer pageCount;
    private List<T> totalKeys;

    private final int PAGE_SIZE = 50;

    public TableData(List<T> totalKeys) {
        this.totalKeys = totalKeys;
        this.pageCount = PageUtil.totalPage(totalKeys.size(), PAGE_SIZE);
    }

    public List<T> getPageData(int pageNo) {
        if (pageNo > pageCount) {
            return Lists.newArrayList();
        }
        if (pageNo == pageCount) {
            return totalKeys.subList((pageNo - 1) * PAGE_SIZE, totalKeys.size());
        }
        return totalKeys.subList((pageNo - 1) * PAGE_SIZE, pageNo * PAGE_SIZE);
    }
}
