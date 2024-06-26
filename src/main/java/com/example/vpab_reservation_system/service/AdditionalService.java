package com.example.vpab_reservation_system.service;

import com.example.vpab_reservation_system.dto.AdditionalDTO;
import com.example.vpab_reservation_system.model.Additional;
import com.example.vpab_reservation_system.repository.AdditionalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.vpab_reservation_system.util.SecurityUtil.isAdmin;

@Service
@RequiredArgsConstructor
public class AdditionalService {
    private final AdditionalRepository additionalRepository;


    public void setToAvailable(Long id) throws IllegalAccessException {
        if (isAdmin()) {
            Optional<Additional> additionOptional = additionalRepository.findById(id);

            if (additionOptional.isPresent()) {
                Additional additional = additionOptional.get();
                additional.setAvailable(true);
                additionalRepository.save(additional);
            } else {
                throw new EntityNotFoundException("User not found with id " + id);
            }
        } else {
            throw new IllegalAccessException("Only ADMIN can demote users to regular users.");
        }
    }

    public void setToUnavailable(Long id) throws IllegalAccessException {
        if (isAdmin()) {
            Optional<Additional> additionOptional = additionalRepository.findById(id);

            if (additionOptional.isPresent()) {
                Additional additional = additionOptional.get();
                additional.setAvailable(false);
                additionalRepository.save(additional);
            } else {
                throw new EntityNotFoundException("User not found with id " + id);
            }
        } else {
            throw new IllegalAccessException("Only ADMIN can demote users to regular users.");
        }
    }

    public List<Additional> findAllAvailableAdditinals() throws IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {
            return additionalRepository.findByAvailableTrue();
        } else {
            throw new IllegalAccessException("Not Authenticated User");
        }
    }


    public List<Additional> findAllAdditionals() throws IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()){
            return additionalRepository.findAll();
        }else{
            throw new IllegalAccessException("Not Authenticated User");
        }
    }

    public Optional<Additional> findAdditionalById(Long id) {
        return Optional.ofNullable(additionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Additional not found by id: " + id)));
    }

    @Transactional
    public Additional saveAdditional(AdditionalDTO additionalDTO) throws IllegalAccessException {
        if (isAdmin()) {
            Additional additional = new Additional();
            BeanUtils.copyProperties(additionalDTO, additional);
            return additionalRepository.save(additional);
        } else {
            throw new IllegalAccessException("Not Authenticated User");
        }
    }
    @Transactional
    public Additional updateAdditional(Long id, AdditionalDTO additionalDTO) {
        Additional existingAdditional = additionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Additional not found by id: " + id));

        if (isAdmin()) {
            BeanUtils.copyProperties(additionalDTO, existingAdditional);
            return additionalRepository.save(existingAdditional);
        } else {
            throw new RuntimeException("Permission denied");
        }
    }
    public void deleteAdditional(Long id) throws IllegalAccessException {
        if(isAdmin()){
         additionalRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Additional not found with id" + id));
         additionalRepository.deleteById(id);
        } else{
            throw new IllegalAccessException("Permission denied");
        }
    }

    public List<Additional> findAllAdditionalsSortedByName() {
        return additionalRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Additional> findAllAdditionalsSortedByPrice() {
        return additionalRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    public List<Additional> findAllAdditionalsSortedByAvailability() {
        return additionalRepository.findByAvailableTrue();
    }


}
