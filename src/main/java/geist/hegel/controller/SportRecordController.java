package geist.hegel.controller;

import com.baomidou.mybatisplus.plugins.Page;
import geist.hegel.common.R;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportRecord;
import geist.hegel.service.SportRecordService;
import geist.hegel.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/records")
public class SportRecordController {

    @Resource
    private SportRecordService recordService;

    @GetMapping("/list")
    public R<Page<SportRecord>> listEvents(@ModelAttribute SportRecord cond) {
        Page<SportRecord> result = recordService.queryPage(cond);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<SportRecord> listEvents(@PathVariable Long id) {
        return R.ok(recordService.selectById(id));
    }

    @PostMapping("/save")
    public R<Object> saveRecord(@RequestBody SportRecord record) {
        record.setUserId(UserUtils.getUserId());
        record.setCreatedAt(LocalDateTime.now());
        recordService.insert(record);
        return R.ok(null);
    }

    @PutMapping("/update")
    public R<Object> updateRecord(@RequestBody SportRecord record) {
        record.setUserId(UserUtils.getUserId());
        record.setUpdatedAt(LocalDateTime.now());
        recordService.updateById(record);
        return R.ok(null);
    }

    @DeleteMapping("/delete/{id}")
    public R<Object> deleteRecord(@PathVariable Long id) {
        recordService.deleteById(id);
        return R.ok(null);
    }

}
