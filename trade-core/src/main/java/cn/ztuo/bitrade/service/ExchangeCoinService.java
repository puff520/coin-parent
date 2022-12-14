package cn.ztuo.bitrade.service;

import com.querydsl.core.types.Predicate;

import cn.ztuo.bitrade.dao.ExchangeCoinRepository;
import cn.ztuo.bitrade.entity.ExchangeCoin;
import cn.ztuo.bitrade.pagination.Criteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import java.util.List;

@Service
public class ExchangeCoinService {
    @Autowired
    private ExchangeCoinRepository coinRepository;

    public List<ExchangeCoin> findAllEnabled() {
        Specification<ExchangeCoin> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> enable = root.get("enable");
            criteriaQuery.where(criteriaBuilder.equal(enable, 1));
            return null;
        };
        Sort sort = Sort.by("sort").ascending();
        return coinRepository.findAll(spec, sort);
    }

    public List<ExchangeCoin> findAllByFlag(int flag) {
        Specification<ExchangeCoin> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> enable = root.get("enable");
            Path<Integer> flagPath = root.get("flag");
            criteriaQuery.where(criteriaBuilder.equal(enable, 1));
            criteriaQuery.where(criteriaBuilder.equal(flagPath, flag));
            return null;
        };
        Sort sort = Sort.by("sort").ascending();
        return coinRepository.findAll(spec, sort);
    }

    public ExchangeCoin findOne(String id) {
        return coinRepository.findById(id).get();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletes(String[] ids) {
        for (String id : ids) {
            coinRepository.deleteById(id);
        }
    }

    public ExchangeCoin save(ExchangeCoin exchangeCoin) {
        return coinRepository.save(exchangeCoin);
    }

    public Page<ExchangeCoin> pageQuery(int pageNo, Integer pageSize) {
        Sort sort = Sort.by("sort");
        PageRequest pageRequest =  PageRequest.of(pageNo - 1, pageSize, sort);
        return coinRepository.findAll(pageRequest);
    }

    public ExchangeCoin findBySymbol(String symbol) {
        return coinRepository.findBySymbol(symbol);
    }

    public List<ExchangeCoin> findAll() {
        return coinRepository.findAll();
    }

    public boolean isSupported(String symbol) {
        return findBySymbol(symbol) != null;
    }

    public Page<ExchangeCoin> findAll(Predicate predicate, Pageable pageable) {
        return coinRepository.findAll(predicate, pageable);
    }

    public List<String> getBaseSymbol() {
        return coinRepository.findBaseSymbol();
    }

    public List<String> getCoinSymbol(String baseSymbol) {
        return coinRepository.findCoinSymbol(baseSymbol);
    }

    public List<String> getAllCoin(){
        return coinRepository.findAllCoinSymbol();
    }

}
