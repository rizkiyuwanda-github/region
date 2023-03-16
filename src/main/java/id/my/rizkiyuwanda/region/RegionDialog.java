package id.my.rizkiyuwanda.region;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.textfield.TextField;
import id.my.rizkiyuwanda.api.RegionWebClientService;
import id.my.rizkiyuwanda.utility.RFormLayout;

public class RegionDialog extends Dialog {

    private final RegionWebClientService regionWebClientService;

    private final Region regionForUpdate;
    private final Button refresh;

    private final TextField name = new TextField("Region Name");
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    public RegionDialog(RegionWebClientService regionWebClientService, Region regionForUpdate, Button refresh) {
        this.regionWebClientService = regionWebClientService;
        this.regionForUpdate = regionForUpdate;
        this.refresh = refresh;
        initLayouts();
        initEvents();
    }

    private void initLayouts(){
        setWidth("500px");
        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);


        RFormLayout rFormLayout = new RFormLayout("400px", 2);
        rFormLayout.add(name, 2);
        Scroller scroller = new Scroller(rFormLayout);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle().set("margin-right", "auto");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(scroller);

        getFooter().add(cancel, save);

        if (regionForUpdate == null) {
            setHeaderTitle("Create");
        } else {
            setHeaderTitle("Update");
            name.setValue(regionForUpdate.getName());
        }
    }

    private void initEvents(){
        cancel.addClickListener(buttonClickEvent -> close());
        save.addClickListener(buttonClickEvent -> saveRegion());
    }

    private void saveRegion() {
        if (name.isEmpty()) {
            Notification.show("Name cannot be empty", 5000, Notification.Position.MIDDLE);
        } else {
            Region region= new Region();
            if(regionForUpdate != null){
                region.setId(regionForUpdate.getId());
            }
            region.setName(name.getValue());
            if(regionWebClientService.save(region) != null){
                Notification.show("Region saved", 5000, Notification.Position.MIDDLE);
                refresh.click();
                this.close();
            }else{
                Notification.show("Error", 5000, Notification.Position.MIDDLE);
            }
        }
    }

}
