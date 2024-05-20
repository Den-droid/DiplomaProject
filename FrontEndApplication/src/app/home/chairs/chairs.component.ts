import { Component, ViewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import {
  ApexAxisChartSeries, ApexChart, ApexDataLabels, ApexFill, ApexLegend, ApexNoData, ApexPlotOptions,
  ApexStroke, ApexTooltip, ApexXAxis, ApexYAxis, ChartComponent
} from 'ng-apexcharts';
import { Faculty } from 'src/app/shared/models/faculty.model';
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
  selector: 'app-home-chairs',
  templateUrl: './chairs.component.html',
  styleUrls: ['./chairs.component.css']
})
export class ChairsComponent {
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

  faculties: Faculty[] = [];

  selectedFaculty = 0;

  showChart = false;

  constructor(private readonly facultyService: FacultyService,
    private readonly scientometricSystemService: ScientometricSystemService,
    private readonly titleService : Title
  ) {
    this.titleService.setTitle("Faculty Chairs Indices");
    this.chartOptions = {
      noData: { text: "Loading..." },
      legend: {},
      series: [],
      chart: {
        type: "bar",
        height: 385,
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
        width: 2,
        colors: ["transparent"]
      },
      xaxis: {
        categories: [
        ]
      },
      yaxis: {
        title: {
          text: "Faculty Indices"
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

    this.facultyService.getAll().subscribe({
      next: (data: Faculty[]) => {
        this.faculties = data;

        if (this.faculties.length > 0) {
          this.selectedFaculty = this.faculties[0].id;
        }
      }
    });

  }

  getIndices() {
    this.showChart = true;

    this.facultyService.getFacultyChairsIndices(this.selectedScientometricSystem, this.selectedFaculty).subscribe({
      next: (facultyChairsIndices: EntityIndices[]) => {
        let citationData: number[] = [];
        let hirshData: number[] = [];
        let categories: string[] = [];

        for (let facultyChairIndices of facultyChairsIndices) {
          categories.push(facultyChairIndices.name);
          citationData.push(facultyChairIndices.indices.citationIndex);
          hirshData.push(facultyChairIndices.indices.hirshIndex);
        }

        this.chart.updateOptions({
          xaxis: { categories: categories }
        });
        this.chart.updateSeries([{ name: "Citation", data: citationData }, { name: "H Index", data: hirshData }]);
      }
    })
  }

}
