package id.my.rizkiyuwanda.region;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import id.my.rizkiyuwanda.api.RegionWebClientService;
import id.my.rizkiyuwanda.utility.RFormLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Region")
@Route(value = "region")
@RouteAlias(value = "")
public class RegionView extends VerticalLayout {
    private final RegionWebClientService regionWebClientService;

    private final RFormLayout rFormLayout = new RFormLayout("400px", 2);
    private final TextField searchTextField = new TextField();
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());
    private final Button addButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
    private final List<Region> regions = new ArrayList<>();
    private final Grid<Region> regionGrid = new Grid<>(Region.class, false);

    PaginatedGrid<Region, ?> regionPaginatedGrid = new PaginatedGrid<>();

    @Autowired
    public RegionView(RegionWebClientService regionWebClientService) {
        this.regionWebClientService = regionWebClientService;
        initLayouts();
        initEvents();
        loadData();
    }

    private void initLayouts() {
        searchTextField.setMinWidth("80px");
        searchTextField.setPlaceholder("Search");
        searchTextField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchTextField.setValueChangeMode(ValueChangeMode.EAGER);

        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.setWidthFull();
        layout1.addAndExpand(searchTextField);
        layout1.add(addButton);

        regionGrid.setHeight("260px");
        regionPaginatedGrid.setHeight("260px");

        initGrid();
        initPaginationGrid();
        rFormLayout.add(layout1, 2);
        rFormLayout.add(regionGrid, 2);
        rFormLayout.add(new VerticalLayout(), 2);
        rFormLayout.add(new Label("Paginate Grid"), 2);
        rFormLayout.add(regionPaginatedGrid, 2);

        add(rFormLayout);
    }

    private void delete(Long id) {
        String message = regionWebClientService.deleteById(id);
        Notification.show(message, 5000, Notification.Position.MIDDLE);
        refreshButton.click();
    }

    private void initGrid() {
        regionGrid.addComponentColumn((region) -> {
            Button menuButton = new Button(VaadinIcon.MENU.create());
            ContextMenu contextMenu = new ContextMenu(menuButton);
            contextMenu.setOpenOnClick(true);
            contextMenu.addItem("Update", e -> new RegionDialog(regionWebClientService, region, refreshButton).open());
            contextMenu.addItem("Delete", e -> delete(region.getId()));

            return menuButton;
        }).setHeader(refreshButton).setFlexGrow(0).setWidth("60px").setFrozen(true);

        regionGrid.addColumn((region) -> {
            return region.getId();
        }).setHeader("ID").setSortable(true).setResizable(true).setWidth("150px");

        regionGrid.addColumn((region) -> {
            return region.getName();
        }).setHeader("Region Name").setSortable(true).setResizable(true).setAutoWidth(true);

        GridListDataView<Region> dataView = regionGrid.setItems(regions);
        searchTextField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(region -> {
            String searchTerm = searchTextField.getValue().trim();
            if (searchTerm.isEmpty()) {
                return true;
            }

            boolean nameSearch = matchesTerm(region.getName(), searchTerm);
            //boolean cariProdukNama = matchesTerm(produkSampahKita.getProduk().getNama(), searchTerm);

            //return cariKategoriNama || cariProdukNama;
            return nameSearch;
        });
    }

    private void initPaginationGrid(){
        regionPaginatedGrid.addColumn(Region::getId).setHeader("ID");
        regionPaginatedGrid.addColumn(Region::getName).setHeader("Region Name");

        regionPaginatedGrid.setItems(regions);

        // Sets the max number of items to be rendered on the grid for each page
        regionPaginatedGrid.setPageSize(8);

        // Sets how many pages should be visible on the pagination before and/or after the current selected page
        regionPaginatedGrid.setPaginatorSize(5);
    }
    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void initEvents() {
        addButton.addClickListener(event -> new RegionDialog(regionWebClientService, null, refreshButton).open());
        refreshButton.addClickListener(event -> loadData());
    }

    private void loadData() {
        searchTextField.clear();
        regions.clear();
        regions.addAll(regionWebClientService.findAll());

        regionGrid.getListDataView().refreshAll();
        regionPaginatedGrid.refreshPaginator();
    }
}
