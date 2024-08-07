package com.maxzamota.spring_sandbox.model_assemblers;

import com.maxzamota.spring_sandbox.controllers.ProductController;
import com.maxzamota.spring_sandbox.model.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<ProductEntity, EntityModel<ProductEntity>> {
    @Override
    public CollectionModel<EntityModel<ProductEntity>> toCollectionModel(Iterable<? extends ProductEntity> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

    @Override
    public EntityModel<ProductEntity> toModel(ProductEntity entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(ProductController.class).get(entity.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAll(Pageable.ofSize(10))).withRel("all")
        );
    }
}
