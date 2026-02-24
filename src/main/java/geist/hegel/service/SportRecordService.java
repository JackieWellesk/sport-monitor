package geist.hegel.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportRecord;

public interface SportRecordService extends IService<SportRecord> {
    Page<SportRecord> queryPage(SportRecord cond);
}