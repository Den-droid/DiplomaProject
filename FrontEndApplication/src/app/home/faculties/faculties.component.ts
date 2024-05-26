import { Component, ViewChild } from '@angular/core';
import {
  ApexAxisChartSeries, ApexChart, ApexDataLabels, ApexFill, ApexLegend, ApexNoData, ApexPlotOptions,
  ApexStroke, ApexTooltip, ApexXAxis, ApexYAxis, ChartComponent
} from 'ng-apexcharts';
import { EntityIndices } from 'src/app/shared/models/indices';
import { ScientometricSystem, mapStringToScientometricSystemLabel } from 'src/app/shared/models/scientometric.model';
import { FacultyService } from 'src/app/shared/services/faculty.service';
import { ScientometricSystemService } from 'src/app/shared/services/scientometric-system.service';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  dataLabels: ApexDataLabels;
  plotOptions: ApexPlotOptions;
  yaxis: ApexYAxis;
  xaxis: ApexXAxis;
  fill: ApexFill;
  tooltip: ApexTooltip;
  stroke: ApexStroke;
  legend: ApexLegend;
  noData: ApexNoData;
};

@Component({
  selector: 'app-home-faculties',
  templateUrl: './faculties.component.html',
  styleUrls: ['./faculties.component.css']
})
export class FacultiesComponent {
  _chart!: ChartComponent;

  @ViewChild(ChartComponent) set chart(chartComponent: ChartComponent) {
    if (chartComponent != undefined && chartComponent != null) {
      this._chart = chartComponent;
    }
  }

  get chart(): ChartComponent {
    return this._chart;
  }

  public chartOptions!: ChartOptions;

  scientometricSystems: ScientometricSystem[] = [];
  selectedScientometricSystem = 0;

  showChart = false;

  constructor(private readonly facultyService: FacultyService,
    public scientometricSystemService: ScientometricSystemService
  ) {
    this.chartOptions = {
      noData: { text: "Завантаження..." },
      legend: {},
      series: [],
      chart: {
        type: "bar",
        height: 500,
        toolbar: {
          show: false
        }
      },
      plotOptions: {
        bar: {
          horizontal: false,
          columnWidth: "70%",
        }
      },
      dataLabels: {
        enabled: false
      },
      stroke: {
        show: true,
        width: 3,
        colors: ["transparent"]
      },
      xaxis: {
        categories: [
        ]
      },
      yaxis: {
        title: {
          text: "Індекси факультетів"
        }
      },
      fill: {
        opacity: 1
      },
      tooltip: {
        y: {
          formatter: function (val: number) {
            return val + "";
          }
        }
      }
    };

    this.scientometricSystemService.getAllScientometricSystems().subscribe({
      next: (data: ScientometricSystem[]) => {
        for (let scientometricSystem of data) {
          scientometricSystem.name = mapStringToScientometricSystemLabel(scientometricSystem.name);
          this.scientometricSystems.push(scientometricSystem);
        }
        if (this.scientometricSystems.length > 0) {
          this.selectedScientometricSystem = this.scientometricSystems[0].id;
        }
      }
    })
  }

  getIndices() {
    this.showChart = true;

    this.facultyService.getFacultiesIndices(this.selectedScientometricSystem).subscribe({
      next: (facultiesIndices: EntityIndices[]) => {
        let citationData: number[] = [];
        let hirshData: number[] = [];
        let categories: string[] = [];

        for (let facultyIndices of facultiesIndices) {
          categories.push(facultyIndices.name);
          citationData.push(facultyIndices.indices.citationIndex);
          hirshData.push(facultyIndices.indices.hirshIndex);
        }

        this.chart.updateOptions({ xaxis: { categories: categories } });
        this.chart.updateSeries([{ name: "Цитування", data: citationData }, { name: "Індекс Гірша", data: hirshData }]);
      }
    })
  }

}
