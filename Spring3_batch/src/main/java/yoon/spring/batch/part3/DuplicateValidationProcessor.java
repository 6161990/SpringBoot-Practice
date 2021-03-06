package yoon.spring.batch.part3;

import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DuplicateValidationProcessor<T> implements ItemProcessor<T, T> {

    private final Map<String, Object> keyPool = new ConcurrentHashMap<>(); //중복 체크할 수 있는 맵
    private final Function<T, String> keyExtractor;
    private final boolean allowDuplicate; // 필터링 여부를 설정하는 값

    public DuplicateValidationProcessor(Function<T, String> keyExtractor,
                                        boolean allowDuplicate) {
        this.keyExtractor = keyExtractor;
        this.allowDuplicate = allowDuplicate;
    }

    @Override
    public T process(T item) throws Exception {
        if (allowDuplicate) { //필터를 하지 않겠다! 바로 넘기기
            return item;
        }

        String key = keyExtractor.apply(item); //해당 item의 key를 추출

        if(keyPool.containsKey(key)){ //해당 key가 keyPool에 존재한다면 null return
            return null;
        }

        keyPool.put(key, key); // 존재하지 않는다면 key를 put
        return item;
    }

    /**
     * keyExtractor 는 person 의 name 뿐만 아니라 다른 field로도 중복체크 할 수 있도록 확장을 고려하였고,
     * 제네릭 T 타입을 Person 타입 뿐만아니라 다른 클래스 타입도 해당 프로세서를 이용할 수 있도록 확장을 고려하였다.
     */
}


/**
 * step은 chunk 한 개 기준으로 트랜잭션이 동작하기 때문에 에러가 발생전에 수행됐던 동작은 롤백되지않고 정상처리 된다.
 * items 100 , chunk.size = 10 , 총 chunk 동작 횟수 = 10
 * chunk 1-9는 정상처리, chunk 10에서 Exception이 발생한 경우
 * chunk 1-9에서 처리된 데이터는 정상 저장되고, Job과 Step의 상태는 FAILED 처리
 * 배치 재 실행 시 chunk 10부터 처리할 수 있도록 배치를 만든다.
 */