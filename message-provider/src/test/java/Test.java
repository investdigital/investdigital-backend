import com.oxchains.message.common.RestResp;
import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.service.PollService;
import com.oxchains.message.service.SaveMessageImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author luoxuri
 * @create 2018-02-07 15:21
 **/
@RestController
@RequestMapping(value = "/test")
public class Test {

    @Resource
    private SaveMessageImpl saveMessage;
    @Resource
    private PollService pollService;


    @PostMapping(value = "/save")
    public RestResp test(@RequestBody MessageVO messageVO) {
        try {
            boolean result = saveMessage.saveMessage(messageVO);
            if (result) {
                return RestResp.success("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResp.fail();
    }

    @DeleteMapping(value = "/delete/{messageId}")
    public RestResp testDel(@PathVariable Long messageId) {
        try {
            return pollService.deleteNoticeMsg(messageId);
        } catch (Exception e){
            e.printStackTrace();
        }
        return RestResp.fail();
    }

}
