package bz.voter.management.election

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Hlayout

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import h5chart.H5Chart
import h5chart.Pie

import bz.voter.management.VoterElection
import bz.voter.management.Election
import bz.voter.management.Division
import bz.voter.management.PollStation
import bz.voter.management.Voter

class VotesChartComposer extends GrailsComposer {

    def chartBox
    def votersChartPanel

    def chartsCount
    def division

    def afterCompose = { window ->
        def electionId = Executions.getCurrent().getArg().electionId

        Election election = Election.get(electionId.toLong())

        division = Division.findByName(ConfigurationHolder.config.division)

        chartBox.getChildren().clear()


        for(PollStation pollStation : PollStation.findAllByDivision(division)){
            def results = VoterElection.getCountOfVotesByElectionAndPollStation(election, pollStation)
            displayChart(pollStation,results)
        }


    }

    

    def displayChart(pollStation,results){
        H5Chart chart = new H5Chart()
        chart.width = "300"
        chart.height = "300"
        chart.setRegionManager(true)

        Pie pie = new Pie()
        pie.setLeft("20")
        pie.setTop("4")
        pie.setWidth("200")
        pie.setHeight("200")
        pie.setAnimate(true)
        pie.setPizza(false)
        pie.setAnimateType(Pie.ANIMATION_RADIAL)

	    def allVoters = Voter.totalVotersByPollStation(pollStation)

        results.each{
            pie.addValue((it[0]/allVoters)*100, "${it[1]}")
        }

        chart.appendChild(pie)
        
        Hlayout hlayout = new Hlayout()
        chartBox.appendChild(hlayout)

        hlayout.appendChild(chart)

    }
}
