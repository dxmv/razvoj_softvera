module studsluzba_dto {
    requires static lombok;
    requires com.fasterxml.jackson.annotation;
    exports org.raflab.studsluzba.model;
    exports org.raflab.studsluzba.model.dto;
}
