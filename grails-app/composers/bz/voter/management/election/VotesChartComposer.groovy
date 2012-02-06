package bz.voter.management.election

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Hlayout
import org.zkoss.zul.Messagebox

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import h5chart.H5Chart
import h5chart.Pie

import bz.voter.management.VoterElection
import bz.voter.management.Election
import bz.voter.management.Division
import bz.voter.management.PollStation
import bz.voter.management.Voter
import bz.voter.management.Affiliation
import bz.voter.management.zk.ComposerHelper
import bz.voter.management.utils.PickupTimeEnum

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VotesChartComposer extends GrailsComposer {

    def chartBox
    def tableBox
    def votesSummaryGrid
    def votesSummaryRows
    def hourlyCountGrid
    def hourlyCountRows
    def hourlyCountColumns
    def hourlyCountHeader
    def votersChartPanel
    def pollStationListbox
    def votesBtn

    def chartsCount
    Division division
    PollStation pollStation
    Election election
    def affiliations

    Hlayout hlayout


    def voterElectionService

    def afterCompose = { window ->
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            def electionId = Executions.getCurrent().getArg().electionId
            affiliations = Affiliation.list()

            election = Election.get(electionId.toLong())

            division = Division.findByName(ConfigurationHolder.config.division)

            chartBox.getChildren().clear()
            for(PollStation pollStation : PollStation.findAllByDivision(division)){
                pollStationListbox.append{
                    listitem(value: pollStation, selected:false){
                        listcell(label: pollStation.pollNumber)
                        listcell(label: pollStation.id)
                    }
                }
        }
        }else{
            ComposerHelper.permissionDeniedBox()
        }

    }


    def onClick_votesBtn(){
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            pollStation = pollStationListbox.getSelectedItem()?.getValue()
            if(pollStation){
                def results = VoterElection.getCountOfVotesByElectionAndPollStation(election, pollStation)
                chartBox.getChildren().clear()
                votesSummaryRows.getChildren().clear()
                displayChart(pollStation, results)
            }else{
                Messagebox.show("Kindly Select a Poll Station!", "Charts Message", Messagebox.OK,
                    Messagebox.EXCLAMATION)
            }
        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }

    

    def displayChart(pollStation,results){
        H5Chart chart = new H5Chart()
        chart.width = "180"
        chart.height = "180"
        chart.setRegionManager(true)

        Pie pie = new Pie()
        pie.setLeft("20")
        pie.setTop("2")
        pie.setWidth("150")
        pie.setHeight("150")
        pie.setAnimate(true)
        pie.setPizza(false)
        pie.setAnimateType(Pie.ANIMATION_RADIAL)

	    def allVoters = Voter.totalVotersByPollStation(pollStation)

        results.each{
            pie.addValue((it[0]/allVoters)*100, "${it[1]}")
        }

        chart.appendChild(pie)
        
        hlayout = new Hlayout()
        chartBox.appendChild(hlayout)

        hlayout.appendChild(chart)

        gridSetup()

    }


    def gridSetup(){
	    def allVoters = Voter.totalVotersByPollStation(pollStation)

        def votesSummary = VoterElection.getCountOfVotesByElectionAndPollStation(election, pollStation)

        def pollStationTotalVotes = 0

        votesSummaryRows.append{
            for(votes in votesSummary){
                row{
                    label(value: "${votes[1]}", style:"font-size: 0.75em")
                    label(value: "${votes[0]}", style: "font-size: 0.75em")
                    label(value: "${(votes[0]/allVoters) * 100} %", style:"font-size: 0.75em")
                    label(value: "${allVoters}", style:"font-size: 0.75em")
                }
                pollStationTotalVotes += votes[0]
            }
            def percentOfVotersWhoVoted = (pollStationTotalVotes/allVoters)*100
            row(style: 'background-color: khaki'){
                label(value: "Total", style:"font-size: 0.75em")
                label(value: "${pollStationTotalVotes}", style:"font-size: 0.75em")
                label(value: "${percentOfVotersWhoVoted}%", style:"font-size: 0.75em")
                label(value: "${allVoters} ", style:"font-size: 0.75em")
            }
       }


        hourlyCountHeader.colspan = affiliations.size()
        hourlyCountColumns.append{
            column{
                label(value:"Hour", class:"gridHeaders")
            }
            for(affiliation in affiliations){
                column{
                    label(value:"${affiliation.name}", class:"gridHeaders")
                }
            }
        }

        def voteCounts = voterElectionService.countByHourAndPollStation(election,division, pollStation)
        println "\nvoteCounts: ${voteCounts}"
        hourlyCountRows.append{
            for(hourVote in voteCounts){
                switch(hourVote.vote_time){
                    case "16.0":
                        println "\n${hourVote.affiliation}"
                        row{
                            label(value: "${PickupTimeEnum.FOUR.value()}", class:"voteCountLabels")
                            //label(value: "${PickupTimeEnumValue}", class="voteCountLabels")
                        }
                        break

                    case "17":
                        println "\n${hourVote.affiliation}"
                        row{
                            label(value: "${PickupTimeEnum.FIVE.value()}", class:"voteCountLabels")
                        }
                        break

                }
            }
        }


       votesSummaryGrid.visible = true
       hourlyCountGrid.visible = true


    }

}
