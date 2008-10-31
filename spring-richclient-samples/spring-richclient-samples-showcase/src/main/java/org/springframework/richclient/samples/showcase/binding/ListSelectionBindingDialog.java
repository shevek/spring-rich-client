package org.springframework.richclient.samples.showcase.binding;

import org.springframework.binding.value.support.RefreshableValueHolder;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.selection.binding.ListSelectionDialogBinder;
import org.springframework.richclient.selection.binding.support.LabelProvider;
import org.springframework.rules.closure.Closure;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class ListSelectionBindingDialog extends TitledApplicationDialog {

	private RefreshableValueHolder refreshableTownValueHolder;

	private List<Country> countries;

	private Map<Country, List<Town>> towns;

	private class Country {
		private String name;

		public Country(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private class Town {
		private String name;

		public Town(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	protected void init() {
		Country belgium = new Country("Belgium");
		Country netherlands = new Country("Netherlands");
		Country germany = new Country("Germany");
		countries = new ArrayList<Country>();
		countries.add(belgium);
		countries.add(netherlands);
		countries.add(germany);
		towns = new HashMap<Country, List<Town>>();
		List<Town> belgiumTowns = new ArrayList<Town>();
		belgiumTowns.add(new Town("Ghent"));
		belgiumTowns.add(new Town("Antwerp"));
		towns.put(belgium, belgiumTowns);
		List<Town> netherlandsTowns = new ArrayList<Town>();
		netherlandsTowns.add(new Town("Eindhoven"));
		netherlandsTowns.add(new Town("Amsterdam"));
		towns.put(netherlands, netherlandsTowns);
		List<Town> germanyTowns = new ArrayList<Town>();
		germanyTowns.add(new Town("Dortmund"));
		germanyTowns.add(new Town("Keulen"));
		towns.put(germany, germanyTowns);
	}

	public List<Country> getCountries() {
		return countries;
	}

	public List<Town> getTowns(Country country) {
		return towns.get(country);
	}

	private class Selection {
		private Country country;
		private Town town;
		private List<Town> towns = new ArrayList<Town>();

		public void setCountry(Country country) {
			this.country = country;
		}

		public Country getCountry() {
			return country;
		}

		public void setTown(Town town) {
			this.town = town;
		}

		public Town getTown() {
			return town;
		}

		public List<Town> getTowns() {
			return towns;
		}

		public void setTowns(List<Town> towns) {
			this.towns = towns;
		}
	}

	private class ListSelectionBindingForm extends AbstractForm {

		public ListSelectionBindingForm() {
			super(FormModelHelper.createFormModel(new Selection()));
		}

		protected JComponent createFormControl() {
			SwingBindingFactory bf = new SwingBindingFactory(getFormModel());
			TableFormBuilder formBuilder = new TableFormBuilder(bf);
			formBuilder.row();

			Map<String, Object> countryContext = new HashMap<String, Object>();
			countryContext.put(ListSelectionDialogBinder.SELECTABLE_ITEMS_HOLDER_KEY, new ValueHolder(countries));
			countryContext.put(ListSelectionDialogBinder.LABEL_PROVIDER_KEY, new LabelProvider() {
				public String getLabel(Object item) {
					Country country = (Country) item;
					return country == null ? "" : country.getName();
				}
			});
			countryContext.put(ListSelectionDialogBinder.FILTERED_KEY, Boolean.TRUE);
			countryContext.put(ListSelectionDialogBinder.FILTER_PROPERTIES_KEY, new String[] { "name" });
		    
			// this works ... but unfortunately ListSelectionDialogBinder has no public constructor
//          ListSelectionDialogBinder binder = new ListSelectionDialogBinder();
//          Binding binding = binder.bind(getFormModel(), "country", countryContext);
//          formBuilder.add(binding, "colSpan=2");

            // this works if the binderSelectionStrategy is configured in richclient-application-context.xml
            formBuilder.add(bf.createBinding("country", countryContext), "colSpan=2");

            formBuilder.row();

			this.addFormValueChangeListener("country", new ChangeCountryListener());

			refreshableTownValueHolder = new RefreshableValueHolder(new Closure() {
				public Object call(Object object) {
					Country country = (Country) getValue("country");
					List<Town> towns = getTowns(country);
					if (towns == null) {
                        towns = Collections.EMPTY_LIST;
                    }
					return towns;
				}
			}, true, false);
			refreshableTownValueHolder.setValue(Collections.EMPTY_LIST);
			formBuilder
					.add(bf.createBoundComboBox("town", refreshableTownValueHolder, "name"), "colSpan=2 align=left");
			formBuilder.row();

			return formBuilder.getForm();
		}
	}

	private class ChangeCountryListener implements PropertyChangeListener {
		public ChangeCountryListener() {
		}

		public void propertyChange(final PropertyChangeEvent evt) {
			refreshableTownValueHolder.refresh();
		}
	}

	protected JComponent createTitledDialogContentPane() {
		Form form = new ListSelectionBindingForm();
		return form.getControl();
	}

	protected boolean onFinish() {
		return true;
	}

}